(ns assignment3.turtle
  (:require [clojure.java.io :as io])
  (:require [quil.core :as q]))


(def c (->> (clojure.string/split (slurp "/Users/PriyaTrishul/assignment3/src/assignment3/turtleprogram.txt") #"\n")
  (map #(clojure.string/split % #" "))
  (map (fn [[k v]] (hash-map :command k :val v)))
  (into [])))


(def numb (count c ))
(def step (atom 0))
(def penval (atom 1))
(def message (atom "No Moves"))
(def pressedkey (atom :s))


(defn move[val]
  (if(= @pressedkey :right)(reset! message (str "move " val)))
  (if (= @penval 0)
  (do (q/stroke 255 255 255)(q/line 0 0 val 0)
  (q/translate val 0))
  (do(q/stroke 0 0 0)(q/line 0 0 val 0)
  (q/translate val 0))))


(defn turn[val]
  (if(= @pressedkey :right)(reset! message (str "turn " val)))
  (q/rotate(q/radians val))

  )


(defn pen-position[val]
  (cond

  (= val 0)(do(if(= @pressedkey :right)(reset! message (str "pen up")))(reset! penval 0))
  (= val 1)(do (if(= @pressedkey :right)(reset! message (str "pen down")))(reset! penval 1))


  ))


(defn keyboard-action []
(let [key (q/key-as-keyword)]
   (reset! pressedkey key)
  (cond
   (= key :right)(if (< @step numb)(swap! step inc))
   (= key :r)(reset! step numb)
   (= key :left)(if (> @step 0)
    (do(let [y (nth c (dec @step))]
    (cond
    (= (:command y) "move")(reset! message (str "undo move " (:val y)))
    (= (:command y) "turn")(reset! message (str "undo turn " (:val y)))
    (= (:command y)"pen")(reset! message (str "undo pen " (:val y)))
     ))(swap! step dec))))
  ))


(defn setup []
(q/frame-rate 200)
  )

(defn draw-state []
(q/background 255)
  (q/fill 0)
  (q/stroke 0 0 0)
  (q/scale 1.5)
  (q/text @message 5 190)

   (if (> @step 0)

   (let [x (subvec c 0 @step)]
     (pen-position 1)
    (doseq [y x]
    (cond
    (= (:command y)"move")(move (read-string(:val y)))
    (= (:command y)"turn")(turn (read-string(:val y)))
    (= (:command y)"pen")(if (= (:val y)"up")(pen-position 0)(pen-position 1))
    )))))

  ;(if (= @pressedkey :r)
  ;(doseq [y c]
    ;(cond
    ;(= (:command y)"move")(move (read-string(:val y)))
    ;(= (:command y)"turn")(turn (read-string(:val y)))
    ;(= (:command y)"pen")(if (= (:val y)"up")(penmove 0)(penmove 1))
    ;)(swap! step numb))))




(q/defsketch quil-test :title "Turtle Program"
:size [300 300]
:setup setup
:draw draw-state
:key-pressed keyboard-action
:features [:keep-on-top])



