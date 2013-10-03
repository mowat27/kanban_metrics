(ns kanban-metrics.sources.excel
  (:require [incanter.excel :refer :all]
            [clojure.string :as str]
            [kanban-metrics.time :refer [->datetime]])
  (:import  java.text.SimpleDateFormat
            java.util.Date))

(defmulti format-cell class)
(defmethod format-cell Date     [date] (->datetime date))
(defmethod format-cell String   [s]    (if-not (str/blank? s) s))
(defmethod format-cell :default [x]    x)

(defn format-row [row]
  (into {} (for [[col value] row] [col (format-cell value)])))

(defn symbolize [coll]
  (vec
    (map #(keyword (str/lower-case (str/replace % #"\s+" "-"))) coll)))

(defn load-sheet [file sheet]
  (map format-row (:rows (read-xls file :sheet sheet))))

(defn all-cols [xls]
  (apply hash-set (apply concat (map keys xls))))

(defn get-all [m cols]
  (map #(get m %) cols))

(defn complete? [cols row]
  (every? (complement nil?) (get-all row cols)))

(defn ->board [xls]
  (let [cols (all-cols xls)]
    (vector
      (symbolize cols)
      (apply concat (map #(get-all % cols) xls)))))

(defn load-excel-file [file sheet]
  (let [xls  (load-sheet file sheet)
        cols (all-cols xls)]
    (->board (filter #(complete? cols %) xls))))
