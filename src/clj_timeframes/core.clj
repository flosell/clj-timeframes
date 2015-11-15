(ns clj-timeframes.core
  (:require [clj-time.core :as t]))

(defn- merge-interval [earlier-start-interval later-end-interval]
  (t/interval (t/start earlier-start-interval) (t/end later-end-interval)))


(defn- should-merge? [a b]
  (or
    (t/overlaps? a b)
    (t/abuts? a b)))

(defn-  merge-internal [current sorted-remaining]
  (cond
    (empty? sorted-remaining) #{current}
    (should-merge? current (first sorted-remaining)) (let [new-cur (merge-interval current (first sorted-remaining))
                                                         rest-result (merge-internal new-cur (rest sorted-remaining))]
                                                     rest-result)
    :else (let [rest-result (merge-internal (first sorted-remaining) (rest sorted-remaining))]
                                                           (conj rest-result current))))

(defn merge-intervals [intervals]
  (let [sorted (sort-by t/start intervals)]
    (merge-internal (first sorted) (rest sorted))))
