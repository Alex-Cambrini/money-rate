# Relazione Finale – Progetto Android: MoneyRate

## 1. Scopo del progetto

MoneyRate è un’app Android pensata per una conversione valutaria in tempo reale e la gestione di un
portafoglio virtuale multivaluta.
Si rivolge a utenti che vogliono convertire rapidamente valute e gestire un portafoglio.

Sviluppata come progetto accademico, non prevede autenticazione né pubblicazione. L’interfaccia è in
inglese.

## 2. Funzionalità principali

- Conversione valutaria in tempo reale, basata su tassi aggiornati online (via API Frankfurter.app)
- Gestione di un wallet virtuale, con valute e importi personalizzabili
- Grafici visuali interattivi: Donut chart per rappresentare la composizione del portafoglio, Bar
  chart per confrontare i tassi di cambio
- Tema scuro e tema chiaro adattivo

## 3. Architettura e struttura del progetto

Il progetto applica i principi della **Clean Architecture**, separando chiaramente:

- logica di dominio (modelli, use case, repository astratti)
- logica dati/API (implementazioni repository, Retrofit, Room)
- interfaccia utente (Jetpack Compose, ViewModel, Navigation)

### Approccio architetturale

- **MVVM (Model-View-ViewModel)**: per la gestione della logica di presentazione
- **Clean Architecture**: per una separazione netta delle responsabilità
- **Dependency Injection manuale**: tramite `UseCaseProvider` e `RepositoryProviderImpl`

### Struttura modulare

- `app/`: entry point dell’applicazione, configurazione generale
- `composeui/`: schermate, componenti UI, temi e navigazione
- `data/`: persistenza locale e API remote
- `domain/`: logica di business e astrazioni

## 4. Componenti chiave

### Tecnologie e librerie utilizzate

- **Jetpack Compose**: interfaccia utente moderna e reattiva
- **Room**: persistenza locale dei dati
- **Retrofit + Moshi**: comunicazione con le API di cambio valuta
- **StateFlow + ViewModel**: gestione dello stato e del ciclo di vita
- **WorkManager**: aggiornamento automatico dei tassi
- **Material 3**: design system coerente con Android moderno

## 5. Comunicazione con API

MoneyRate interroga il servizio Frankfurter API (https://api.frankfurter.app/) per ottenere:

- la lista delle valute disponibili
- i tassi di cambio aggiornati tra valute

La comunicazione avviene tramite Retrofit con parser JSON Moshi.

## 6. Persistenza dati

L’app utilizza Room per la gestione del database locale, con tre entità:

- `CurrencyEntity`: lista delle valute disponibili
- `CurrencyRateEntity`: cache dei tassi di cambio
- `WalletEntryEntity`: entry del wallet utente

Questi dati vengono aggiornati e gestiti tramite Dao dedicati e repository implementati nel modulo
data.

## 7. UI e UX

L’interfaccia è sviluppata interamente in Jetpack Compose, con tre schermate principali.  
Per migliorare la leggibilità e la riusabilità del codice UI, i componenti Composable riutilizzabili
sono stati separati in una cartella components/, distinta da screens/ che contiene le schermate
principali. Questa divisione favorisce la modularità dell’interfaccia e la manutenzione del codice.

- SplashScreen: animazione iniziale e caricamento dati
- HomeScreen: conversione valuta e grafici dei tassi
- WalletScreen: gestione visiva del portafoglio

### Esperienza utente

- Supporto automatico a tema scuro/chiaro
- Input numerico flessibile: supporto sia a punto che virgola come separatore decimale, per
  un'esperienza utente naturale
- Layout responsive e animazioni coerenti con le linee guida Material 3

## 8. Gestione aggiornamenti: WorkManager

L’app aggiorna periodicamente i tassi di cambio ogni **15 minuti** usando **WorkManager**, con
vincoli di rete attiva (`NetworkType.CONNECTED`).

### Comportamento

- All’avvio, viene schedulato un `PeriodicWorkRequest` con `ExistingPeriodicWorkPolicy.KEEP`
- L’aggiornamento richiede una connessione di rete attiva
- È prevista una **backoff policy esponenziale** in caso di errore
- I dati sono salvati in cache locale per permettere l’uso offline
- I tassi vengono aggiornati anche all’apertura dell’app per maggiore affidabilità

## 9. Punti di forza

- Architettura solida, modulare e facilmente estendibile
- UI moderna con grafici ben integrati
- Aggiornamenti automatici dei dati ottimizzati per il ciclo di vita del sistema
- Codice leggibile e ben separato in livelli
- Utilizzo corretto di coroutine per operazioni asincrone

## 10. Problematiche riscontrate

### Limitazioni WorkManager

- Su alcuni dispositivi, l’esecuzione periodica fallisce perché il sistema disattiva la connessione
  internet (modalità **Doze**, ottimizzazione batteria).

- **Soluzione scartata**: disabilitare l’ottimizzazione batteria via codice (
  `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`).  
  Sebbene efficace, è una misura invasiva e rischiosa per la pubblicazione sul Play Store.

- **Soluzione adottata**: configurare WorkManager con vincolo `NetworkType.CONNECTED` e backoff
  esponenziale; aggiornare i tassi anche all’avvio dell'app per garantire un refresh sicuro.  
  Questa strategia è conforme alle policy di sistema.

### Gestione localizzazione numerica

- I tassi dalle API usano il punto (`.`) come separatore decimale, mentre l’utente italiano si
  aspetta la virgola (`,`).
- Il `ViewModel` normalizza l’input con `replace(',', '.')` per accettare entrambi i formati (es.
  `1.5` e `1,5`)
- La `HomeScreen` formatta l’output con `NumberFormat(Locale.ITALY)` per mostrare i valori nel
  formato atteso (es. `1,2345`)

Questo garantisce compatibilità input/output e coerenza nell’interfaccia.

## 11. Estensioni future

Funzionalità utili da considerare per un'evoluzione futura dell'app:

- Cronologia delle conversioni
- Grafici temporali con andamento dei tassi
- Notifiche su soglie personalizzate
- Autenticazione utente (es. login con Google/Firebase)
- Sincronizzazione cloud del wallet

## 12. Dettagli del team

Il progetto è stato realizzato da:

- Alex Cambrini
- Lorenzo Ferrari

## 13. Conclusioni

**MoneyRate** è un’app Android completa e moderna, pensata per offrire un’esperienza utente fluida e
una struttura tecnica robusta.  
L'utilizzo di **Jetpack Compose**, **WorkManager**, e una **Clean Architecture modulare** ha
garantito una buona manutenibilità e chiarezza del codice.

La separazione dei livelli ha favorito una collaborazione efficiente e un'app pronta per eventuali
estensioni o pubblicazione.

## 14. Riferimenti

- API: https://www.frankfurter.app/
- Android Developers: https://developer.android.com/
- Jetpack Compose: https://developer.android.com/jetpack/compose  
