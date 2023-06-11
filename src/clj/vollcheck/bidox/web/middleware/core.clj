(ns vollcheck.bidox.web.middleware.core
  (:require
    [vollcheck.bidox.env :as env]
    [ring.middleware.defaults :as defaults]
    [ring.middleware.session.cookie :as cookie]
    [ring.middleware.cors :as cors]))

(defn wrap-base
  [{:keys [_metrics site-defaults-config cookie-secret] :as opts}]
  (let [cookie-store (cookie/cookie-store {:key (.getBytes ^String cookie-secret)})]
    (fn [handler]
      (cond-> ((:middleware env/defaults) handler opts)
              true (defaults/wrap-defaults
                     (assoc-in site-defaults-config [:session :store] cookie-store))
              ))))

(defn wrap-db [{:keys [db] :as _opts}]
  (fn [handler]
    (fn [request]
      (-> request
          (assoc :db db)
          (handler)))))

(defn wrap-cors [& access-control]
  (fn [handler]
    (let [access-control (cors/normalize-config access-control)]
      (fn [request]
        (cors/handle-cors handler request access-control cors/add-access-control)))))
