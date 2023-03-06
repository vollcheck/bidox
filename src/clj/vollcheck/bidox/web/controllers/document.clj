(ns vollcheck.bidox.web.controllers.document
  (:require
   [muuntaja.core :as m]
   [ring.util.http-response :refer [ok]]
   [xtdb.api :as xt]
   [vollcheck.bidox.web.commons :as commons]))

(defn get-all-documents
  [_]
  (-> (xt/db (commons/get-xt-node))
      (xt/q '{:find [id title]
           :where [[doc :doc/title title]
                   [doc :xt/id id]]})
      (ok)))

(defn get-document
  [req]
  (let [{:keys [title ts]}
        (->> req :body slurp (m/decode "application/json"))

        ts (when ts
             (clojure.instant/read-instant-date ts))]
    (-> (if ts
          (xt/db (commons/get-xt-node) ts)
          (xt/db (commons/get-xt-node)))
        (xt/q '{:find [(pull doc [:xt/id :doc/title :doc/content])]
                :where [[doc :doc/title given-title]]
                :in [given-title]}
              title)
        (ok))))
