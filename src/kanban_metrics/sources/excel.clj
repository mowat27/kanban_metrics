(ns kanban-metrics.sources.excel
  (:require [incanter.core :refer :all]
            [incanter.excel :refer :all]
            [clojure.string :as str]))

(defn loader [& cols]
  (let [full-row? (fn [row] (<= (count cols) (count row)))]
    (fn [file sheet]
      (let [xls (read-xls file :sheet sheet)]
        (->> xls
             ($where full-row?)
             ($ cols)
             :rows
             (map vals)
             (apply concat))))))

(def file "/Users/adrianmowat/temp/green_team_metrics.xlsx")
(def cols ["Requests" "Backlog" "In Progress" "QA" "UAT" "Done"])
(def sheet "Kanban")

(defn symbolize [& coll]
  (vec
    (map #(keyword (str/lower-case (str/replace % #"\s+" "-"))) coll)))

(defn load-excel-file [file]
  (let [loader-fn (apply loader cols)]
    (->> (loader-fn file sheet)
         (map str)
         vec
         (vector (apply symbolize cols)))))