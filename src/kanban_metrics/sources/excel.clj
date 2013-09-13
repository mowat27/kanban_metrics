(ns kanban-metrics.sources.excel
  (:require [incanter.core :refer :all]
            [incanter.excel :refer :all]
            [clojure.string :as str]
            [clj-time.core :refer [date-time]])
  (:import  java.text.SimpleDateFormat
            java.util.Date))

(defn ->date-time [date]
  (date-time (.getYear date)
             (.getMonth date)
             (.getDay date)
             (.getHours date)
             (.getMinutes date)))

(defmulti format-cell class)
(defmethod format-cell Date     [date] (->date-time date))
(defmethod format-cell String   [s]    (if-not (str/blank? s) s))
(defmethod format-cell :default [x]    x)

(defn format-row [row]
  (into {} (for [[col value] row] [col (format-cell value)])))

(def file "/Users/adrianmowat/temp/green_team_metrics.xlsx")

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
    (vector (symbolize cols)
            (->> (map #(get-all % cols) xls)
                 (map #(vec (vals %)))
                 (apply concat)
                 vec))))

(defn load-excel-file [file sheet]
  (let [xls  (load-sheet file sheet)
        cols (all-cols xls)]
    (->board (filter #(complete? cols %) xls))))