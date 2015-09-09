(ns porwigle.handler.public
  (:gen-class)
  (require [porwigle.core :as porwigle]))


(defn handle-request [request]
  (let [pagebody (porwigle/eval-page (porwigle/pagestructure (:uri request)))]
    ; exception would be nice
    (if
      (clojure.string/blank? pagebody)
      {:status 404
       :body nil}

      {:status 200
       :body pagebody})))
