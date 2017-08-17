(ns assignment3.sample
  (:require [quil.core :as q]))

(def message (atom "No keyboard"))


(defn keyboard-action []
(let [key (q/key-as-keyword)]
  (reset! message (str "Key " key))
  (if (= key :r)
(q/start-loop) (q/redraw))))



(defn setup []
  (q/frame-rate 20)
  (q/no-loop))


(defn draw-state []
(q/background 255)
(q/no-fill)
(q/scale 2.5)
(q/translate 30 30)
(q/text @message 0 0)
(q/text (str (q/frame-count)) 0 20))
(q/defsketch quil-test
:title "Lines"
:size [300 300]
:setup setup
:draw draw-state
:key-pressed keyboard-action
:features [:keep-on-top])
