(ns kanban-metrics.board-test
  (:require [midje.sweet :refer :all]
            [kanban-metrics.board :refer :all]
            [clj-time.core  :as t]))

(def board
  [[:backlog  :dev  :done]
   ["1/6"     "2/6" "3/6"]])

(def expected-rows
  [(t/date-time 2000 6 1)
   (t/date-time 2000 6 2)
   (t/date-time 2000 6 3)])

(def prep
  (prepper {:datetime-format "dd/MM"}))

(fact (prep board) => [(first board) expected-rows])


(def monday    (.toDate (t/date-time 2013 9 7)))
(def tuesday   (.toDate (t/date-time 2013 9 8)))
(def wednesday (.toDate (t/date-time 2013 9 9)))
(def thursday  (.toDate (t/date-time 2013 9 10)))
(def friday    (.toDate (t/date-time 2013 9 11)))

(def columns [:backlog, :dev, :done])
(def card1 {:backlog monday,
            :dev tuesday,
            :done wednesday})

(def card2 {:backlog monday,
            :dev tuesday,
            :done wednesday})

(def card3 {:backlog tuesday,
            :dev tuesday,
            :done tuesday})

(fact (columns-on-date [:backlog :dev :done] card1 monday)    => #{:backlog} )
(fact (columns-on-date [:backlog :dev :done] card3 monday)    => #{} )
(fact (columns-on-date [:backlog :dev :done] card3 tuesday)   => #{:dev :backlog :done} )
(fact (columns-on-date [:backlog :dev :done] card3 wednesday) => #{:done} )

(def my-columns-on-date (partial columns-on-date [:backlog :dev :done]))

(fact (reverse-map 1 #{:x :y}) =>   [:y 1 :x 1])
(fact (reverse-map [1 #{:x :y}]) => [:y 1 :x 1])

(facts "about finding the state of a board on a date"
  (board-on-date my-columns-on-date monday  [card1] )  => {:backlog [card1]}
  (board-on-date my-columns-on-date tuesday [card1 card2] ) => {:backlog [nil], :dev [card1 card2], :done [nil]}
  )

