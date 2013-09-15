(ns kanban-metrics.core
  (:require [kanban-metrics.sources.excel :as e]
            [kanban-metrics.metrics :as m])
  (:gen-class))

(defn -main [& argv]
  (let [file  (first argv)
        sheet (second argv)
        board (e/load-excel-file file sheet)]
    (println (str "Done/day: "  (int (m/cards-per-day board :done))))
    (println (str "Cycle time") (int (m/cycle-time    board :done 8)))))
