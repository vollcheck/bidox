(ns vollcheck.bidox.web.controllers.document
  (:require
   [clojure.instant :refer [read-instant-date]]
   [muuntaja.core :as m]
   [ring.util.http-response :refer [ok]]
   [xtdb.api :as xt]))

(defn get-all-documents
  [req]
  (when-let [xt-node (:db req)]
    (-> (xt/db xt-node)
        (xt/q '{:find [id title] ;; that could be (pull doc [:xt/id :doc/title])
                :keys [id title]
                :where [[doc :doc/title title]
                        [doc :xt/id id]]})
        (ok))))

(comment
  (-> integrant.repl.state/system
      :db.xtdb/node
      xt/db
      (xt/q '{:find [id title content]
              :keys [id title content]
              :in [given-id]
              :where [[doc :xt/id given-id]
                      [doc :xt/id id]
                      [doc :doc/title title]
                      [doc :doc/content content]]}
            1002)
      )


  #_(let [{:keys [title timestamp]} (->> req :body slurp (m/decode "application/json"))
          timestamp (when timestamp (read-instant-date timestamp))]
      (-> (if timestamp
            (xt/db xt-node timestamp)
            (xt/db xt-node))
          ))

  )

(defn get-document
  [req]
  (if-let [xt-node (:db req)]
    (let [id (-> req :path-params :id parse-long)]
      (-> (xt/db xt-node)
          (xt/q '{:find [id title content]
                  :keys [id title content]
                  :in [given-id]
                  :where [[doc :xt/id given-id]
                          [doc :xt/id id]
                          [doc :doc/title title]
                          [doc :doc/content content]]}
                id)
          first ok))
    (ok))) ;; TODO: do you really want to return anything if there's no db?

(defn put-document
  [req]
  (if-let [xt-node (:db req)]
    (let [{:keys [title content timestamp]} (->> req :body slurp (m/decode "application/json"))
          timestamp (when timestamp (read-instant-date timestamp))]
      (-> (xt/db xt-node timestamp)
          (xt/submit-tx [[::xt/put {:doc/title title
                                    :doc/content content}]])
          (ok)))
    (ok)))
