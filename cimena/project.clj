(defproject cimena "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring-server "0.4.0"]
                 [selmer "0.8.2"]
                 [com.taoensso/timbre "3.4.0"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.65"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [compojure "1.3.3"]
                 [ring/ring-defaults "0.1.4"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.5.0"]
                 [noir-exception "0.2.3"]
                 [bouncer "0.3.2"]
                 [prone "0.8.1"]
                 [ragtime "0.3.8"]
                 [yesql "0.5.0-rc1"]
                 [clj-http "1.1.1"]
                 [cheshire "5.4.0"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]]

  :min-lein-version "2.0.0"
  :uberjar-name "cimena.jar"
  :repl-options {:init-ns cimena.handler}
  :jvm-opts ["-server"]

  :main cimena.core

  :plugins [[lein-ring "0.9.1"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]
            [ragtime/ragtime.lein "0.3.8"]]




  :ring {:handler cimena.handler/app
         :init    cimena.handler/init
         :destroy cimena.handler/destroy
         :uberwar-name "cimena.war"}

  :ragtime
  {:migrations ragtime.sql.files/migrations
   :database
   "jdbc:postgresql://localhost/cimena_db?user=cimena&password=cimena"}




  :profiles
  {:uberjar {:omit-source true
             :env {:production true}

             :aot :all}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.7.0"]
                        ]
         :source-paths ["env/dev/clj"]



         :repl-options {:init-ns cimena.repl}
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}})
