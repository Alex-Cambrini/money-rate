# Relazione Finale – Progetto Android: MoneyRate

## 1. Scopo del progetto

MoneyRate è un’app Android pensata per una conversione valutaria in tempo reale e la gestione di un
portafoglio virtuale multivaluta.
Si rivolge a utenti che vogliono convertire rapidamente valute e gestire un portafoglio simulato.

Sviluppata come progetto accademico, non prevede autenticazione né pubblicazione. L’interfaccia è in
inglese.

## 2. Funzionalità principali

- Conversione valutaria in tempo reale, basata su tassi aggiornati online (via API Frankfurter.app)
- Gestione di un wallet virtuale, con valute e importi personalizzabili
- Grafici visuali interattivi: Donut chart per rappresentare la composizione del portafoglio, Bar
  chart per confrontare i tassi di cambio
- Tema scuro e tema chiaro adattivo

## 3. Architettura e struttura del progetto

Il progetto applica i principi della Clean Architecture, separando chiaramente:

- logica di dominio (modelli, use case, repository astratti)
- logica dati/API (implementazioni repository, Retrofit, Room)
- interfaccia utente (Jetpack Compose, ViewModel, Navigation)

La struttura modulare è suddivisa in quattro moduli principali:

**Moduli principali:**

- `app/`: CustomApplication, WorkManager, WorkerFactory
- `composeui/`: schermate, ViewModel, temi, navigation, components
- `data/`: implementazioni repository, chiamate API, database Room
- `domain/`: modelli, repository astratti, casi d’uso

## 4. Componenti chiave utilizzati

**Librerie e tecnologie:**

- Jetpack Compose – interfaccia utente reattiva
- Room – persistenza locale dei dati del wallet
- Retrofit + Moshi – comunicazione con API Frankfurter
- WorkManager – aggiornamento automatico dei tassi
- StateFlow + ViewModel – gestione stato e ciclo vita
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

Questi dati vengono aggiornati e gestiti tramite Dao dedicati e repository implementati nel modulo
data.

## 6. Comunicazione con API

MoneyRate interroga il servizio Frankfurter API (https://api.frankfurter.app/) per ottenere:

- la lista delle valute disponibili
- i tassi di cambio aggiornati tra valute

La comunicazione avviene tramite Retrofit con parser JSON Moshi.

## 7. UI e UX

L’interfaccia è realizzata interamente in Jetpack Compose, con tre schermate principali:

- SplashScreen: schermata iniziale con animazione e caricamento dei dati
- HomeScreen: conversione valuta e visualizzazione grafica dei tassi di cambio
- WalletScreen: gestione del portafoglio con rappresentazione visiva della composizione

L’interfaccia supporta automaticamente il tema scuro e chiaro, adattandosi alle preferenze del
sistema operativo per un’esperienza visiva coerente e confortevole.

## 8. Gestione aggiornamenti: WorkManager

- All’avvio, l’app configura UseCaseProvider e schedula un aggiornamento periodico dei tassi ogni 15
  minuti con WorkManager.
- L’aggiornamento richiede una connessione di rete (NetworkType.CONNECTED) e usa una policy di
  backoff esponenziale in caso di errore.
- I tassi aggiornati vengono salvati in cache locale per garantire l’uso offline.
- Se il lavoro è già schedulato, non viene duplicato (ExistingPeriodicWorkPolicy.KEEP).

## 9. Punti di forza

- Architettura pulita, facilmente estendibile
- UI moderna e reattiva
- Grafici interattivi ben integrati
- Aggiornamento automatico dati via WorkManager
- Codice modulare e leggibile
- Uso corretto di coroutine per operazioni asincrone, evitando blocchi sulla main thread

## 10. Problematiche riscontrate

### Limitazioni WorkManager

- Su alcuni dispositivi, l’esecuzione periodica fallisce perché il sistema disattiva la connessione
  internet (modalità Doze, ottimizzazione batteria).
- Soluzione scartata: disabilitare l’ottimizzazione batteria via codice (
  `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`), efficace ma invasiva e rischiosa per la pubblicazione sul
  Play Store.
- Soluzione adottata: configurare WorkManager con vincolo `NetworkType.CONNECTED` e backoff
  esponenziale; aggiornare i tassi anche all’avvio dell’app per garantire un refresh sicuro.
  Strategia conforme alle policy di sistema.

### Gestione localizzazione numerica

- I tassi dalle API usano il punto (`.`) come separatore decimale, mentre l’utente italiano si
  aspetta la virgola (`,`).
- Il **ViewModel** normalizza l’input con `replace(',', '.')` per accettare entrambi i formati (es.
  `1.5` e `1,5`).
- La **HomeScreen** formatta l’output con `NumberFormat(Locale.ITALY)` per mostrare i valori nel
  formato atteso (es. `1,2345`).

Questo garantisce compatibilità input/output e coerenza nell’interfaccia.

## 11. Possibili estensioni future

Sebbene il progetto sia stato completato con tutte le funzionalità previste, alcune estensioni utili
potrebbero essere:

- Aggiunta di cronologia delle conversioni
- Supporto per grafici temporali (trend dei tassi)
- Aggiunta di notifiche quando una valuta raggiunge una soglia
- Integrazione con Firebase o autenticazione Google

## 12. Dettagli del team

Il progetto è stato realizzato da:

- Alex Cambrini
- Lorenzo Ferrari

## 13. Conclusioni

MoneyRate è un’app Android completa e moderna. L’utilizzo di Compose, WorkManager e un’architettura
modulare ha permesso di ottenere una struttura chiara, facilmente manutenibile e pronta per essere
eventualmente estesa con nuove funzionalità.

La separazione tra livelli domain, data e ui ha favorito una divisione del lavoro efficiente
all’interno del team, con una buona copertura delle problematiche comuni in fase di sviluppo mobile
moderno.

## 14. Riferimenti

- API: https://www.frankfurter.app/
- Android Developers: https://developer.android.com/
- Jetpack Compose: https://developer.android.com/jetpack/compose  
