# Relazione Finale – Progetto Android: MoneyRate

## 1. Scopo del progetto
MoneyRate è un’applicazione mobile Android sviluppata con lo scopo di fornire uno strumento semplice, moderno ed efficace per la conversione valutaria in tempo reale e la gestione di un portafoglio virtuale multivaluta.  
L’app è pensata per utenti che desiderano:
- convertire rapidamente importi tra diverse valute;
- tracciare e gestire un piccolo portafoglio simulato in diverse valute.

MoneyRate è stata sviluppata come progetto accademico e non include funzionalità di autenticazione o pubblicazione. L’interfaccia è completamente in lingua inglese.

## 2. Funzionalità principali
Le principali funzionalità implementate nell’app sono:
- Conversione valutaria in tempo reale, basata su tassi aggiornati online (via API Frankfurter.app)
- Gestione di un wallet virtuale, con valute e importi personalizzabili
- Grafici visuali interattivi: Donut chart per rappresentare la composizione del portafoglio, Bar chart per confrontare i tassi di cambio
- Aggiornamento automatico dei tassi ogni 15 minuti, tramite WorkManager
- Tema scuro e tema chiaro adattivo

## 3. Architettura e struttura del progetto
Il progetto applica i principi della Clean Architecture, separando chiaramente:
- logica di dominio (modelli, use case, repository astratti)
- logica dati/API (implementazioni repository, Retrofit, Room)
- interfaccia utente (Jetpack Compose, ViewModel, navigation)

La struttura modulare è suddivisa in tre moduli principali:

**Moduli principali:**

- `domain/`: modelli, repository astratti, casi d’uso
- `data/`: implementazioni repository, chiamate API, database Room
- `composeui/`: schermate, ViewModel, temi, navigation, MainActivity

**Struttura ad alto livello:**  
money-rate/
├── app/ → Worker (CurrencyUpdateWorker e factory), CustomApplication (entrypoint app)
├── data/ → implementazioni repository, gestione database locale (Room: DAO, entità, database), comunicazione API (Retrofit e modelli di risposta), controllo connettività di rete
├── composeui/ → UI (Jetpack Compose), ViewModel, Screens
├── domain/ → modelli di dominio, interfacce repository e use case, provider per dependency injection manuale


## 4. Componenti chiave utilizzati
**Librerie e tecnologie:**
- Jetpack Compose – interfaccia utente reattiva
- Room – persistenza locale dei dati del wallet
- Retrofit + Moshi – comunicazione con API Frankfurter
- WorkManager – aggiornamento automatico dei tassi
- StateFlow + ViewModel – gestione dello stato e ciclo di vita
- Material 3 – stile coerente e moderno

**Architettura:**
- MVVM (Model-View-ViewModel)
- Clean Architecture (UseCase + Repository pattern)
- Dependency Injection manuale (UseCaseProvider, RepositoryProviderImpl)

## 5. Persistenza dati
L’app utilizza Room per la gestione del database locale, con tre entità:
- `CurrencyEntity`: lista delle valute disponibili
- `CurrencyRateEntity`: cache dei tassi di cambio
- `WalletEntryEntity`: entry del wallet utente

Questi dati vengono aggiornati e gestiti tramite Dao dedicati e repository implementati nel modulo data.

## 6. Comunicazione con API
MoneyRate interroga il servizio Frankfurter API (https://api.frankfurter.app/) per ottenere:
- la lista delle valute disponibili
- i tassi di cambio aggiornati tra valute

La comunicazione avviene tramite Retrofit con parser JSON Moshi.

## 7. UI e UX
L’interfaccia è realizzata interamente in Jetpack Compose, con tre schermate principali:
- SplashScreen: schermata iniziale con animazione e caricamento
- HomeScreen: conversione valuta, grafico bar chart
- WalletScreen: gestione del wallet, donut chart, entry modificabili

La UI supporta nativamente tema scuro/chiaro, grazie all’uso dinamico dei colori Material.

## 8. Gestione aggiornamenti: WorkManager
Al lancio dell’app, la classe CustomApplication registra un worker ricorrente ogni 15 minuti (`CurrencyUpdateWorker`) che:
- recupera i tassi di cambio aggiornati
- aggiorna la cache locale nel database Room
- gestisce eventuali errori o retry

Questo garantisce la presenza di dati aggiornati anche offline.

## 9. Punti di forza
- Architettura pulita, facilmente estendibile
- UI moderna e reattiva
- Grafici interattivi ben integrati
- Aggiornamento automatico dei dati
- Codice modulare e leggibile
- Nessun blocco sulla main thread (uso corretto di coroutine)

## 10. Possibili estensioni future
Sebbene il progetto sia stato completato con tutte le funzionalità previste, alcune estensioni utili potrebbero essere:
- Aggiunta di cronologia delle conversioni
- Supporto per grafici temporali (trend dei tassi)
- Aggiunta di notifiche quando una valuta raggiunge una soglia
- Integrazione con Firebase o autenticazione Google

## 11. Dettagli del team
Il progetto è stato realizzato da:
- Alex Cambrini
- Lorenzo Ferrari

## 12. Conclusioni
MoneyRate è un’app Android completa e moderna. L’utilizzo di Compose, WorkManager e un’architettura modulare ha permesso di ottenere una struttura chiara, facilmente manutenibile e pronta per essere eventualmente estesa con nuove funzionalità.

La separazione tra livelli domain, data e ui ha favorito una divisione del lavoro efficiente all’interno del team, con una buona copertura delle problematiche comuni in fase di sviluppo mobile moderno.

## 13. Riferimenti
- API: https://www.frankfurter.app/
- Android Developers: https://developer.android.com/
- Jetpack Compose: https://developer.android.com/jetpack/compose  
