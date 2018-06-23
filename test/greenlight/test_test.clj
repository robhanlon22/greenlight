(ns greenlight.test-test
  (:require
    [clojure.test :refer :all]
    [com.stuartsierra.component :as component]
    [greenlight.step :as step]
    [greenlight.test :as test]))


(test/deftest sample-greenlight-test
  "A sample greenlight test"
  #::step{:name 'sample-greenlight-test
          :title "Sample greenlight test"
          :inputs {:foo 1
                   :bar 2
                   :baz 3}
          :output [::foo ::bar ::baz]
          :test (fn [{:keys [foo bar baz]}]
                  (is (= 1 foo))
                  (is (= 2 bar))
                  (is (= 3 baz))
                  4)}
  #::step{:name 'another-step
          :title "Another Step"
          :output (fn [ctx outputs]
                    (merge outputs ctx))
          :inputs {:a (step/lookup [::foo ::bar ::baz])
                   :b 5
                   :c (step/component ::component)
                   :double-a (step/lookup (fn [ctx]
                                            (* 2 (get-in ctx [::foo ::bar ::baz]))))}
          :test (fn [{:keys [a b c double-a]}]
                  (is (= 4 a))
                  (is (= 5 b))
                  (is (= 6 c))
                  (is (= (* 2 a) double-a))
                  {:d (* 2 a)
                   :e (* 2 b)
                   :f (* 2 c)})}
  [#::step{:name 'third-step
           :title "Third Step"
           :inputs {:x (step/lookup :d)
                    :y (step/lookup :e)
                    :z (step/lookup :f)}
           :test (fn [{:keys [x y z]}]
                   (is (= 8 x))
                   (is (= 10 y))
                   (is (= 12 z)))}])


(deftest sample-test
  (let [system (component/system-map ::component 6)
        test-result (test/run-test! system (sample-greenlight-test))]
    (is (= :pass (::test/outcome test-result))
        (with-out-str (clojure.pprint/pprint test-result)))
    (is (= 3 (count (::test/steps test-result))))))
