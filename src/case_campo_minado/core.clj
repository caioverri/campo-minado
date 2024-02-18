(ns case-campo-minado.core
  
  (:gen-class))
;; vars
(defn -main []
  (def finalTime (atom 0))
  (def time (atom (System/currentTimeMillis)))
  (def replay (atom "s"))
  (def score (atom 0))
  (def bestScore (atom 0))
  (def bestTime (atom 0))
  (def selectedPositions (atom []))
  (def bombExplosion (atom false))
  (def mineField
    (vec (map vec
              [[false true false]
               [false false false]
               [true false false]]
         )
    )
  )
  
  ;; methods
  (defn clearScreen []
    (print (str (char 27) "[2J"))
    (print (str (char 27) "[;H"))
  )

  (defn welcome []
    (clearScreen)
    (println "Bem vindo ao Campo Minado")
    (println)
    (println "Introducao:")
    (println "O campo minado contem 9 areas, sendo 3 linhas e 3 colunas")
    (println "Primeiramente coloque o numero da linha desejada, depois aperte enter, e entao coloque o numero da coluna desejada ")
    (println)
    (println "Aperte ENTER para iniciar")
    (read-line)
    (clearScreen)
  )
  
  (defn convertToSeconds []
   (let [finallyTime (* (- (System/currentTimeMillis) @time) 0.001)]
     (reset! finalTime finallyTime)
   )
  )
  
  (defn replayGame [] 
   (if (= @replay "s")
    (do
     (clearScreen)
     (reset! score 0)
     (reset! selectedPositions [])
     (reset! bombExplosion false)
     (reset! time (System/currentTimeMillis))
    )
   )
  )

  (defn updateScoreAndTime []
    (do
    (reset! bestScore @score)
    (reset! bestTime @finalTime))   
  )
  
  (defn bestScoreCalculate []
    (if (< @bestScore @score)
      (updateScoreAndTime)
    )
    (if (and (= @bestScore @score) (> @bestTime @finalTime)) 
      (updateScoreAndTime)
    )
  )

  (defn readPosition []
    (println "Digite o numero da linha: ")
    (def line (atom (read-line)))
    (println "Digite o numero da coluna: ")
    (def column (atom (read-line)))
      {:line (Integer. @line)
       :column (Integer. @column)}
  )

  (defn userPosition []
    (def choosedPosition (atom false))
    (while (not @choosedPosition)
      (let [position (readPosition)]
        (if (some #(= % position) @selectedPositions)
          (println "Posicao ja selecionada")
          (do
            (swap! selectedPositions conj position)
            (reset! choosedPosition true)
          )
        )
      )
    )
    (boolean true))

  (defn playGame []
    (replayGame)
    (while (and (< @score 7) (not @bombExplosion))
      (if (userPosition)
        (let [lastPosition (last @selectedPositions)
              {line :line column :column} lastPosition
              cellValue (get-in mineField [line column])]
          (if (= cellValue false)
            (do
              (println "+1 ponto!")
              (println)
              (swap! score inc)
              (when (>= @score 7)
                (do
                  (convertToSeconds)
                  (bestScoreCalculate)
                  (println "Voce venceu! Acertou" @score "ponto(s) em" @finalTime"segundos")
                  (println "Melhor pontuacao:" @bestScore "em" @bestTime "segundos")
                  (println)
                )
              )
            )
            (do
              (clearScreen)
              (convertToSeconds)
              (bestScoreCalculate)
              (println "Pisou em uma mina! Fim de jogo")
              (println)
              (println @score "ponto(s) em" @finalTime "segundos")
              (println "Melhor pontuacao:" @bestScore "em" @bestTime"segundos")
              (println)
              (reset! bombExplosion true)           
            )
          )          
        )
      ) 
    )
   (println "Gostaria de jogar novamente [s] [n]:")
   (reset! replay (read-line))
  )
 
  (defn play []
   (while (= @replay "s")
    (playGame)
   )
   (clearScreen)
   (println "Obrigado por jogar!"))
  
;; program
 (welcome)
 (play)
)