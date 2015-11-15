(ns clj-timeframes.core-test
  (:require [clojure.test :refer :all]
            [clj-timeframes.core :refer :all]
            [clj-time.core :as t]))

(def start-time (t/now))

(def after-5-sec (t/plus start-time (t/seconds 10)))
(def after-ten-sec (t/plus start-time (t/seconds 10)))
(def after-fifteen-sec (t/plus start-time (t/seconds 15)))
(def after-twenty-sec (t/plus start-time (t/seconds 20)))


(deftest merge-intervals-test
  (testing "that a single interval is unchanged"
    (is (= #{(t/interval start-time after-ten-sec)}
           (merge-intervals [(t/interval start-time after-ten-sec)]))))
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
