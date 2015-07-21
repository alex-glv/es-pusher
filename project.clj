(defproject kiries "0.1.0-SNAPSHOT"
  :description "A bundled deployment of Kibana, Riemann, and ElasticSearch"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [riemann-clojure-client "0.3.2"]
                 [cheshire "5.5.0"]
                 [clj-logging-config "1.9.10"]
                 [clj-json "0.5.3"]
                 [slingshot "0.10.3"]
                 [riemann "0.2.9"]
                 [clj-time "0.7.0"]
                 [clojurewerkz/elastisch "1.5.0-beta3"]
                 [metrics-clojure "1.0.1"]
                 ;; web tools
                 [compojure "1.1.8"]
                 [hiccup "1.0.5"]
                 [org.markdownj/markdownj "0.3.0-1.0.2b4"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-devel "1.2.0"]
                 [ring/ring-jetty-adapter "1.2.0"]

                 ;; integration tools
                 [clj-http "0.9.1"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 ]
  
  
  :resource-paths ["resources"]
  ;;:jar-exclusions [#"^config"]
  :main kiries.core
  :profiles {:user {:plugins [[cider/cider-nrepl "0.10.0-SNAPSHOT"]
                              [refactor-nrepl "1.1.0-SNAPSHOT"]
                              ]
                    :dependencies [[org.clojure/tools.nrepl "0.2.10"]]}
             :uberjar {:aot :all, :uberjar-merge-with {#"^META-INF/services/" [slurp str spit]}}}
  :aliases {"server" ["trampoline" "run" "-m" "kiries.core"]})


