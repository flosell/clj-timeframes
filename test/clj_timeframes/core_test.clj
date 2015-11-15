(ns clj-timeframes.core-test
  (:require [clojure.test :refer :all]
            [clj-timeframes.core :refer :all]
            [clj-time.core :as t]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.generators :as gen]
            [com.gfredericks.test.chuck.generators :as cgen]
            [clojure.math.combinatorics :as combo]
            [clojure.test.check.properties :as prop]))

(def start-time (t/now))

(def after-5-sec (t/plus start-time (t/seconds 10)))
(def after-ten-sec (t/plus start-time (t/seconds 10)))
(def after-fifteen-sec (t/plus start-time (t/seconds 15)))
(def after-twenty-sec (t/plus start-time (t/seconds 20)))


(deftest merge-intervals-test
  (testing "that a single interval is unchanged"
    (is (= #{(t/interval start-time after-ten-sec)}
           (merge-intervals [(t/interval start-time after-ten-sec)]))))
  (testing "can handle empty input"
    (is (= #{}
           (merge-intervals []))))
  (testing "that two intervals that dont overlap stay in the end result"
    (is (= #{(t/interval start-time after-ten-sec)
             (t/interval after-fifteen-sec after-twenty-sec)}
           (merge-intervals [(t/interval start-time after-ten-sec)
                             (t/interval after-fifteen-sec after-twenty-sec)]))))
  (testing "that intervals that do overlap are merged together"
    (is (= #{(t/interval start-time after-twenty-sec)}
           (merge-intervals [(t/interval start-time after-ten-sec)
                             (t/interval after-5-sec after-twenty-sec)])))
    (is (= #{(t/interval start-time after-twenty-sec)}
           (merge-intervals [(t/interval after-5-sec after-twenty-sec)
                             (t/interval start-time after-ten-sec)])))))



(def dt
  (cgen/datetime {:offset-fns [clj-time.core/minutes]
                  :offset-min -10
                  :offset-max 10}))

(defn can-be-interval? [[a b]]
  (not (t/after? a b)))

(defn to-interval [[a b]]
  (t/interval a b))

(def interval
  (gen/fmap to-interval
    (gen/such-that can-be-interval?
                   (gen/tuple dt dt)
                   100)))

(def intervals
  (gen/vector interval))

(defn- overlaps? [[a b]]
  (t/overlaps? a b))

(defn contains-overlapping-intervals? [intervals]
  (let [combos (combo/combinations intervals 2)]
    (some overlaps? combos)))


(defspec merging-produces-no-overlapping-intervals 100
         (prop/for-all [i intervals]
                       (not (contains-overlapping-intervals? (merge-intervals i)))))

(defspec merging-doesnt-increase-the-number-of-intervals 100
         (prop/for-all [i intervals]
                       (>= (count i) (count (merge-intervals i)))))
