(ns kanban-metrics.examples.big-board
  (:require [clojure.string :as str]
            [kanban-metrics.board :as board]))

(def columns [:requests :backlog :in-progress :qa :uat :done])

(def months
  { "Jan" "01"
    "Feb" "02"
    "Mar" "03"
    "Apr" "04"
    "May" "05"
    "Jun" "06"
    "Jul" "07"
    "Aug" "08"
    "Sep" "09"
    "Oct" "10"
    "Nov" "11"
    "Dec" "12" })

(def raw-data
"21-Aug  22-Aug  22-Aug  22-Aug  23-Aug  23-Aug
22-Aug  23-Aug  23-Aug  23-Aug  23-Aug  23-Aug
21-Aug  22-Aug  22-Aug  22-Aug  23-Aug  23-Aug
22-Aug  22-Aug  22-Aug  22-Aug  23-Aug  26-Aug
23-Aug  23-Aug  26-Aug  26-Aug  26-Aug  26-Aug
23-Aug  23-Aug  26-Aug  26-Aug  26-Aug  26-Aug
26-Aug  26-Aug  26-Aug  26-Aug  26-Aug  26-Aug
26-Aug  26-Aug  26-Aug  26-Aug  26-Aug  26-Aug
23-Aug  23-Aug  26-Aug  26-Aug  26-Aug  26-Aug
22-Aug  22-Aug  22-Aug  22-Aug  22-Aug  26-Aug
26-Aug  26-Aug  26-Aug  26-Aug  26-Aug  26-Aug
22-Aug  22-Aug  22-Aug  22-Aug  23-Aug  26-Aug
21-Aug  22-Aug  22-Aug  22-Aug  23-Aug  26-Aug
21-Aug  23-Aug  26-Aug  26-Aug  26-Aug  26-Aug
29-Aug  29-Aug  03-Sep  03-Sep  03-Sep  03-Sep
26-Aug  26-Aug  26-Aug  30-Aug  30-Aug  02-Sep
02-Sep  05-Sep  05-Sep  05-Sep  05-Sep  06-Sep
06-Sep  06-Sep  06-Sep  06-Sep  09-Sep  09-Sep
22-Aug  27-Aug  29-Aug  02-Sep  03-Sep  05-Sep
05-Sep  06-Sep  06-Sep  06-Sep  09-Sep  09-Sep
02-Sep  02-Sep  02-Sep  03-Sep  03-Sep  05-Sep
06-Sep  06-Sep  06-Sep  06-Sep  09-Sep  09-Sep
21-Aug  27-Aug  30-Aug  02-Sep  09-Sep  09-Sep
22-Aug  27-Aug  03-Sep  05-Sep  06-Sep  09-Sep
22-Aug  27-Aug  02-Sep  03-Sep  05-Sep  09-Sep
02-Sep  04-Sep  05-Sep  06-Sep  09-Sep  09-Sep
05-Sep  05-Sep  05-Sep  06-Sep  06-Sep  06-Sep
05-Sep  05-Sep  05-Sep  05-Sep  06-Sep  09-Sep
02-Sep  03-Sep  03-Sep  03-Sep  05-Sep  05-Sep
28-Aug  29-Aug  29-Aug  02-Sep  03-Sep  10-Sep
26-Aug  26-Aug  26-Aug  26-Aug  26-Aug  28-Aug
03-Sep  03-Sep  03-Sep  03-Sep  03-Sep  03-Sep
27-Aug  27-Aug  27-Aug  27-Aug  27-Aug  28-Aug
28-Aug  28-Aug  28-Aug  28-Aug  28-Aug  28-Aug
28-Aug  28-Aug  28-Aug  28-Aug  28-Aug  28-Aug
27-Aug  27-Aug  27-Aug  27-Aug  27-Aug  28-Aug
28-Aug  28-Aug  28-Aug  28-Aug  28-Aug  28-Aug
26-Aug  26-Aug  27-Aug  27-Aug  27-Aug  28-Aug")

(defn format-date [s]
  (let [[dd mmm] (str/split s #"-")]
    (str dd "/" (months mmm))))

(defn trans-row [row]
  (->> (map #(if (= "nil" %) nil %) row)
       (map format-date)))

(def board
  (->> (str/split raw-data #"\s+")
       trans-row
       vec
       (vector columns)
       (board/prepare {:datetime-format "dd/MM"})))
