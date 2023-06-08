(ns vollcheck.bidox.web.controllers.document
  (:require
   [clojure.instant :refer [read-instant-date]]
   [muuntaja.core :as m]
   [ring.util.http-response :refer [ok]]
   [xtdb.api :as xt]))

(defn get-all-documents
  [req]
  (if-let [xt-node (:db req)]
    (-> (xt/db xt-node)
        (xt/q '{:find [id title]
                :where [[doc :doc/title title]
                        [doc :xt/id id]]})
        (ok))
    (ok)))

(defn get-document
  [req]
  (if-let [xt-node (:db req)]
    (let [{:keys [title timestamp]} (->> req :body slurp (m/decode "application/json"))
          timestamp (when timestamp (read-instant-date timestamp))]
      (-> (xt/db xt-node timestamp)
          (xt/q '{:find [(pull doc [:xt/id :doc/title :doc/content])]
                  :where [[doc :doc/title given-title]]
                  :in [given-title]}
                title)
          (ok)))
    (ok)))
