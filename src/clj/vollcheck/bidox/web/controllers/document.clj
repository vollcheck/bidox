(ns vollcheck.bidox.web.controllers.document
  (:require
   [clojure.instant :refer [read-instant-date]]
   [muuntaja.core :as m]
   [ring.util.http-response :refer [ok]]
   [xtdb.api :as xt]))

(defn- describe-doc [[id title]]
  {:id id
   :title title})

(defn- make-flat [doc]
  (if (= 1 (count doc))
    (first doc)
    doc))

(defn get-all-documents
  [req]
  (when-let [xt-node (:db req)]
    (-> (xt/db xt-node)
        (xt/q '{:find [id title]
                :where [[doc :doc/title title]
                        [doc :xt/id id]]})
        ((fn [docs] (map describe-doc docs)))
        (ok))))

(defn get-document
  [req]
  (if-let [xt-node (:db req)]
    (let [id (-> req :path-params :id parse-long)]
      (-> (xt/db xt-node)
          (xt/q '{:find [(pull doc [:xt/id :doc/title :doc/content])] ;; (pull doc [*])
                  :in [id]
                  :where [[doc :xt/id id]]}
                id)
          first make-flat ok))

    #_(let [{:keys [title timestamp]} (->> req :body slurp (m/decode "application/json"))
          timestamp (when timestamp (read-instant-date timestamp))]
      (-> (if timestamp
            (xt/db xt-node timestamp)
            (xt/db xt-node))
          ))

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
