;; -*- indent-tabs-mode: nil -*-

(ns midje.midje-forms.building
  (:use [midje.util.wrapping :only [ensure-correct-form-variable]]))

(defn make-fake [fake-body]
  `(midje.semi-sweet/fake ~@fake-body))

(defn make-background [fake]
  (concat fake '(:type :background)))

(defmacro before [wrapping-target before-form & extras ]
  (let [after-form (second extras)]
    (ensure-correct-form-variable `(try
                                    ~before-form
                                    ?form
                                    (finally ~after-form)))))

(defmacro after [wrapping-target after-form]
  (ensure-correct-form-variable `(try ?form (finally ~after-form))))

(defmacro around [wrapping-target around-form]
  (ensure-correct-form-variable around-form))


(def *unfolded-prerequisite-counts*)

(defmacro forgetting-unfolded-prerequisites [& forms]
  `(binding [*unfolded-prerequisite-counts* (atom {})]
     ~@forms))

(defn metaconstant-for-form [inner-form]
  (let [function-symbol (first inner-form)
        swap-fn (fn [current-value function-symbol]
                  (if (current-value function-symbol)
                    (assoc current-value function-symbol
                           (inc (current-value function-symbol)))
                    (assoc current-value function-symbol 1)))
        number ((swap! *unfolded-prerequisite-counts* swap-fn function-symbol)
                function-symbol)]
    (symbol (format "...%s-value-%s..." (name function-symbol) number))))

