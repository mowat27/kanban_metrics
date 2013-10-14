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

(def card4 {:backlog tuesday,
            :dev thursday,
            :done friday})

(facts "about get-bounds"
  (get-bounds [monday tuesday])   => [monday monday]
  (get-bounds [monday monday])    => [monday monday]
  (get-bounds [monday wednesday]) => [monday tuesday]
  )

(facts "about finding the columns touched by each card on a given date"
  (columns-on-date [:backlog :dev :done] card3 tuesday)   => #{:dev :backlog :done}
  (columns-on-date [:backlog :dev :done] card4 monday)    => #{}
  (columns-on-date [:backlog :dev :done] card4 tuesday)   => #{:backlog}
  (columns-on-date [:backlog :dev :done] card4 wednesday) => #{:backlog}
  (columns-on-date [:backlog :dev :done] card4 thursday)  => #{:dev}
  (columns-on-date [:backlog :dev :done] card4 friday)    => #{:done})

(facts "about finding the state of a board on a date"
  (def my-columns-on-date (partial columns-on-date [:backlog :dev :done]))

  (board-on-date my-columns-on-date monday  [card1])  => {:backlog [card1]}
  (board-on-date my-columns-on-date tuesday [card1 card2]) => {:dev [card1 card2]})


(facts "within?"
  (within? [monday monday] monday)     => true
  (within? [monday monday] tuesday)    => false
  (within? [monday tuesday] tuesday)   => true
  (within? [monday wednesday] tuesday) => true
  (within? [tuesday wednesday] monday) => false)

(facts "about get-cards"
  (get-cards [:x]    {:x [1 2] :y [3 4]}) => [[1 2]]
  (get-cards [:y :x] {:x [1 2] :y [3 4]}) => [[3 4] [1 2]]
  (get-cards [:z]    {:x [1 2] :y [3 4]}) => [[]]
  (get-cards [:x :z] {:x [1 2] :y [3 4]}) => [[1 2] []])

(facts "about padding"
  (pad [1 2] 3)     => [1 2 nil])
  (pad [1 2] 3 "x") => [1 2 "x"]

(facts "about getting the cards for a date"
  (get-cards-padded [:x :y] {:x [1] :y [2]}) => [[1] [2]]
  (get-cards-padded [:x :z] {:x [1] :y [2]}) => [[1] [nil]]
  (get-cards-padded [:x]    {:x [1 2]})      => [[1 2]])

(facts "about converting a column hash to a matrix"
  (map-to-matrix [:x] {:x [1]}) => [[:x]
                                    [ 1]]
  (map-to-matrix [:x] {:x [1 2]}) => [[:x]
                                       [1]
                                       [2]]
  (map-to-matrix [:x :y] {:x [1 2] :y [3 4]})  => [[:x :y]
                                                   [ 1  3]
                                                   [ 2  4]]
  (map-to-matrix [:y :x] {:x [1 2] :y [3 4]})  => [[:y :x]
                                                   [ 3  1]
                                                   [ 4  2]]
  (map-to-matrix [:x :y] {:x [1] :y [3 4]})    => [[:x  :y]
                                                   [ 1   3]
                                                   [ nil 4]]
  (map-to-matrix [:x :y :z] {:x [1] :y [3 4]}) => [[:x  :y :z]
                                                   [ 1   3  nil]
                                                   [ nil 4  nil]])

