(ns drawing.core
  (:require [reagent.core :as r :refer [atom]]
           [schema.core :as s :include-macros true
             ]))

(enable-console-print!)

(defonce app-states (atom{:app-db []
                      :location1 {:x s/Num :y s/Num}
                      :location2 {:x s/Num :y s/Num}
                      :clicks  0
                      :draw-mode :line }))

(defn validate-states[]
  app-states)

(defn draw-line[value] ;function to draw line
  [:line {:x1 (:x1 value) :y1 (:y1 value) :x2 (:x2 value) :y2 (:y2 value)}])

(defn draw-rect[value] ;function to draw rectangle
  [:rect {:x (min (:x1 value) (:x2 value))
          :y (min (:y1 value) (:y2 value))
          :width (js/Math.abs (- (:x1 value)(:x2 value)))
          :height (js/Math.abs (- (:y1 value)(:y2 value)))
          :fill "maroon"
          }])

(defn draw-circle[value] ;function to draw circle
  [:circle {:cx (:x1 value)
            :cy (:y1 value)
            :r  (js/Math.sqrt (+ (js/Math.pow (js/Math.abs (- (:x1 value)(:x2 value))) 2)
                                 (js/Math.pow (js/Math.abs (- (:y1 value)(:y2 value))) 2)))
            :fill "blue"}])

(defn draw []
  (for [each  (:app-db @app-states)]
    (cond
    (= (each 0):line) (draw-line (each 1))
    (= (each 0):rect) (draw-rect (each 1))
    (= (each 0):circle) (draw-circle (each 1))
  )))


(defn draw-mouse [] ;;function to draw shapes on mouse move
  (if (= (:clicks @app-states) 1)
   (cond
    (= (:draw-mode @app-states) :line)(draw-line {:x1 (:x (:location1 @app-states))
                                                  :y1 (:y (:location1 @app-states))
                                                  :x2 (:x (:location2 @app-states))
                                                  :y2 (:y (:location2 @app-states))})
    (= (:draw-mode @app-states) :rect)(draw-rect {:x1 (:x (:location1 @app-states))
                                                  :y1 (:y (:location1 @app-states))
                                                  :x2 (:x (:location2 @app-states))
                                                  :y2 (:y (:location2 @app-states))})
    (= (:draw-mode @app-states) :circle)(draw-circle {:x1 (:x (:location1 @app-states))
                                                      :y1 (:y (:location1 @app-states))
                                                      :x2 (:x (:location2 @app-states))
                                                      :y2 (:y (:location2 @app-states))}))))

(defn undo[] ;function on click of undo
  (cond
   (= ((last (:app-db @app-states)) 0) :mode)
       ((swap! app-states assoc :draw-mode ((last (:app-db @app-states)) 1))
       (swap! app-states assoc :app-db (subvec (:app-db @app-states)0 (- (count (:app-db @app-states)) 1))))
   :else (swap! app-states assoc :app-db (subvec (:app-db @app-states)0 (- (count (:app-db @app-states)) 1)))

   ))


(defn drawing []
[:div
[:svg {:width 600 :height 500 :stroke "black"
:style {:position :fixed :top 80 :left 0 :border "black solid 1px"}
:on-click (fn one-click[e]
            (swap! app-states update-in [:clicks] inc)
            (cond
             (= (:clicks @app-states) 1)((swap! app-states assoc :location1 {:x (s/validate s/Num (.-clientX e))
                                                                             :y (s/validate s/Num (- (.-clientY e) 80))})
                                         (swap! app-states assoc :location2 {:x (s/validate s/Num (.-clientX e))
                                                                             :y (s/validate s/Num (- (.-clientY e) 80))}))

             (= (:clicks @app-states) 2)((swap! app-states assoc :location2 {:x (s/validate s/Num (.-clientX e))
                                                                             :y (s/validate s/Num (- (.-clientY e) 80))})
                                          (swap! app-states assoc :clicks 0);;reset clicks after 2 clicks
                                          ;; store the shape drawn after the second click
                                          (swap! app-states update-in [:app-db] conj [(:draw-mode @app-states){:x1 (:x (:location1 @app-states))
                                                                                                               :y1 (:y (:location1 @app-states))
                                                                                                               :x2 (:x (:location2 @app-states))
                                                                                                               :y2 (:y (:location2 @app-states))}])

                                         )))

:on-mouse-move (fn mouse-move[e](if (= (:clicks @app-states) 1)
                                  (swap! app-states assoc :location2 {:x (s/validate s/Num (.-clientX e))
                                                                      :y (s/validate s/Num (- (.-clientY e) 80))})))
}

 (draw)
 (draw-mouse)
][:button {:on-click (fn line-click[e]
                      ;store the shape mode selected on click
                       ((swap! app-states update-in [:app-db] conj [:mode (:draw-mode @app-states)])
                       (swap! app-states assoc :draw-mode :line)))
             :style {:color (if (= (:draw-mode @app-states) :line) "red")}
           } "Line"]
 [:button {:on-click (fn circle-click[e]
                      ;store the shape mode selected on click
                       ((swap! app-states update-in [:app-db] conj [:mode (:draw-mode @app-states)])
                       (swap! app-states assoc :draw-mode :circle)))
           :style {:color (if (= (:draw-mode @app-states) :circle) "red")}
           } "Circle"]
 [:button {:on-click (fn rect-click[e]
                       ;store the shape mode selected on click
                       ((swap! app-states update-in [:app-db] conj [:mode (:draw-mode @app-states)])
                       (swap! app-states assoc :draw-mode :rect)))
            :style {:color (if (= (:draw-mode @app-states) :rect) "red")}
                       } "Rectangle"]
 [:input {:type "button"
          :on-click undo :disabled (zero? (count (:app-db @app-states)))
          :value (str "Undo (" (count (:app-db @app-states)) ")")}]
 ])


(r/render-component [drawing]
(. js/document (getElementById "app")))


(defn on-js-reload []
  )
