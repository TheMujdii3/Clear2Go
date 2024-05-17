

Clear2Go: Inovația care Revoluționează Comunicațiile Aeronautice

Clear2Go este o soluție inovatoare concepută pentru a aborda și rezolva o problemă veche și persistentă în domeniul comunicațiilor radio. Principalul scop al acestei aplicații este eliminarea majorității comunicațiilor radio între persoanele autorizate să coordoneze mișcarea în anumite sectoare sau perimetre. În acest context, aplicația a fost special dezvoltată pentru a gestiona autorizațiile de bază emise de turnurile de control ale aerodromurilor civile pentru avioane.

Avantajele Implementării Clear2Go
Fluidizarea Traficului Aerian: Clear2Go promite să fluidizeze traficul în anumite zone de zbor, oferind o metodă mai eficientă și sigură de transmitere a informațiilor critice între controlori și piloți. Eliminarea bruiajului și a interferențelor tipice comunicațiilor radio tradiționale asigură că mesajele sunt transmise și primite în mod clar și fără erori.
Reducerea Incidențelor și Accidentelor: Unul dintre cele mai semnificative beneficii ale utilizării Clear2Go este potențialul său de a reduce numărul incidentelor grave sau accidentelor. Prin garantarea faptului că informațiile de la controlor ajung complet și exact la pilot, riscurile asociate cu neînțelegerile sau pierderile de comunicare sunt considerabil diminuate.

Necesitatea Implementării Clear2Go
Necesitatea implementării unui sistem de comunicații prin data-link, precum Clear2Go, a devenit evidentă în sezonul de zbor 2023-2024. Pe parcursul acestui sezon, am avut oportunitatea să zbor cu mai multe avioane ale Aeroclubului României. În aproximativ jumătate dintre acestea, când se realizau transmisiuni pe frecvență, tot ce auzeam era un fâșâit deranjant (noise signal). Această problemă m-a obligat să folosesc aplicații de mesagerie, precum WhatsApp, pentru a clarifica mesajele transmise de turnul de control – o practică nesigură, deoarece necesită să îmi iau ochii de la exterior și de la aparatura avionului.

Beneficiile și Impactul Clear2Go
Eficientizarea Operațiunilor: Clear2Go va elibera frecvențele radio de comunicațiile neesențiale, permițând controlorilor de trafic aerian să se concentreze pe gestionarea situațiilor critice și îmbunătățirea siguranței aeriene.
Integrare Simplificată: Sistemul este proiectat pentru o integrare ușoară cu infrastructurile existente, minimizând necesitatea de investiții majore sau schimbări complicate în procesele operaționale curente.

Publicul Țintă
Publicul țintă pentru Clear2Go este format din aerodromuri de mici dimensiuni in cautarea unei solutii fiable cu un pret competitiv care sa ii asiste in gestioanarea  traficului aerian al propriilor avioane.

Proiectarea arhitecturala:
Am decis să dezvoltăm aplicația utilizând Java, asigurând compatibilitatea cu telefoanele care rulează cel puțin Android 10, fapt ce ne permite să acoperim aproximativ 75% din dispozitivele mobile actuale. Alegerea Java ca limbaj de programare ne oferă stabilitate și o integrare facilă cu ecosistemul Android. 

Infrastructura Backend
În spatele aplicației funcționează un set de SDK-uri (Software Development Kit) furnizate de Firebase, un serviciu dezvoltat de Google, care ne permit să implementăm autentificarea OAuth cu contul dvs. Google și să avem o bază de date live. Firebase oferă o suită completă de instrumente care facilitează dezvoltarea, testarea și monitorizarea aplicației, contribuind astfel la o dezvoltare mai rapidă și mai eficientă.


Structura și Gestionarea Datelor
A doua bază de date este organizată ierarhic și conține informații despre utilizarea aplicației. Aceasta permite adăugarea de implementări suplimentare ale aplicației și gestionarea locațiilor asociate. Pentru transmiterea informațiilor în această bază de date, utilizăm un model key-value (Boolean), similar cu structura de date Map din STL. În situațiile în care avem nevoie de gestionarea unor date mai complexe, folosim o clasă care în prezent conține maxim două componente: requester și requests.

Fluxul de Autorizare și Funcționalități
Pentru a asigura că utilizatorii urmează pașii corecți în procesul de autorizare, am implementat metodele bazei de date Firebase într-o manieră secvențială, astfel încât fiecare pas să fie deblocat succesiv. Acest lucru previne omisiunile neintenționate și asigură un flux coerent al aplicației.

Funcționalități GPS
În prezent, aplicația utilizează exclusiv tehnologia GPS a telefonului pentru a determina altitudinea față de izobara de 1013,2 hPa, viteza de deplasare și poziția (longitudine și latitudine), stocând aceste date online. Această funcționalitate este esențială pentru o gamă variată de aplicații, de la monitorizarea activităților fizice la navigație și multe altele. Tehnologia GPS integrată ne permite să oferim utilizatorilor date precise și în timp real, esențiale pentru funcționalitatea aplicației.

Scalabilitate și Extensibilitate
Una dintre prioritățile noastre principale este asigurarea scalabilității și extensibilității aplicației. Utilizând Firebase, avem posibilitatea de a scala baza de date și infrastructura backend pe măsură ce numărul de utilizatori crește, fără a compromite performanța. În plus, structura modulară a codului ne permite să adăugăm rapid noi funcționalități și să integrăm alte servicii, cum ar fi notificările push, analizele de utilizare și suportul pentru mai multe limbaje. 

Monitorizare și Analiză
Pentru a îmbunătăți continuu aplicația, utilizăm Firebase Analytics, care ne oferă date valoroase despre comportamentul utilizatorilor. Aceste informații ne permit să identificăm rapid problemele, să optimizăm experiența utilizatorilor și să adaptăm aplicația în funcție de feedback-ul primit. 

Securitate și Autentificare
Prima bază de date este destinată stocării utilizatorilor, identificându-i prin intermediul unui token generat aleator după autentificarea prin metoda OAuth. Această metodă elimină vulnerabilitățile asociate autentificării tradiționale prin email și parolă, reducând riscul atacurilor de tip bruteforce, SQL injection și alte tipuri de atacuri cibernetice. Utilizarea token-urilor OAuth asigură un nivel de securitate ridicat și o experiență de utilizare mai sigură și mai convenabilă.
În dezvoltarea unui software destinat gestionării comunicațiilor dintr-un aerodrom, securitatea reprezintă un aspect esențial. Pentru a asigura un nivel ridicat de protecție împotriva unor posibile amenințări, am optat pentru utilizarea standardului OAuth, o soluție care împiedică atacurile de tip injecție sau forță brută, asigurând astfel autentificarea securizată în aplicație.
OAuth este un protocol de autorizare deschis și standardizat, conceput pentru a permite aplicațiilor să acceseze resursele protejate ale unui utilizator fără a fi necesară dezvăluirea credențialelor acestuia. Prin implementarea OAuth în software-ul nostru, am eliminat riscul ca datele sensibile să fie expuse la amenințări precum injecțiile SQL sau atacurile brute force, oferind în schimb un sistem robust și securizat de autentificare.
Pentru a completa stratul de securitate al comunicațiilor între piloți și controlori, ne-am bazat pe bazele de date Firebase, care oferă facilități puternice de criptare a datelor. Folosind algoritmul SHA-256, toate transmisiunile sunt criptate înainte de a fi trimise către bazele de date Firebase, asigurând astfel confidențialitatea și integritatea informațiilor în tranzit.
Firebase este o platformă de dezvoltare de aplicații mobile și web oferită de Google, care include o gamă largă de servicii, inclusiv bază de date în timp real, autentificare, stocare în cloud și multe altele. Utilizând Firebase pentru securitatea comunicațiilor, beneficiem de infrastructura solidă și actualizată oferită de Google, reducând astfel riscurile asociate cu posibilele vulnerabilități ale unor sisteme mai vechi sau mai puțin fiabile. 




Prin combinarea standardului OAuth pentru autentificare și a serviciilor criptografice oferite de Firebase pentru securizarea comunicațiilor, software-ul nostru destinat gestionării comunicațiilor din aerodrom asigură un nivel înalt de securitate și protecție împotriva amenințărilor cibernetice din ce în ce mai sofisticate. Această abordare proactivă în materie de securitate ne permite să ne concentrăm pe îmbunătățirea funcționalităților și performanțelor aplicației, știind că datele și comunicările utilizatorilor sunt în siguranță.

Interfata utilizatorului
În urma sondării opiniilor piloților, am decis să folosim un UI (User Interface) cât mai simplu pentru aplicația noastră. Această decizie se bazează pe nevoia de a comunica doar strictul necesar, asigurând astfel o interacțiune scurtă și la obiect.
Prin simplificarea interfeței, dorim să:
Îmbunătățim eficiența operațională: Piloții vor putea accesa rapid informațiile esențiale, fără a fi distrași de elemente inutile sau complexe.
Reduce timpul de răspuns: O interfață minimalistă permite o navigare mai rapidă, reducând timpul necesar pentru completarea sarcinilor.
Minimalizăm riscul de eroare: O interfață clară și simplă reduce confuzia și erorile, ceea ce este crucial într-un domeniu unde precizia este esențială.
Creăm o experiență de utilizare intuitivă: Prin eliminarea elementelor redundante, piloții pot interacționa cu aplicația fără a necesita instruiri suplimentare extinse.
În concluzie, simplificarea UI-ului reflectă un răspuns direct la feedback-ul primit de la piloți și subliniază angajamentul nostru de a crea o aplicație eficientă, intuitivă și adaptată nevoilor reale ale utilizatorilor noștri.

Suport și Actualizări
Planificăm să oferim suport continuu pentru utilizatori, prin actualizări regulate și rezolvarea promptă a problemelor raportate. În plus, vom adăuga noi funcționalități în funcție de nevoile și cerințele utilizatorilor, menținând aplicația relevantă și utilă în timp. 

Stabilitatea aplicatiei:
Stabilitatea si eficienta aplicatiei noastre a fost o prioritate de la începutul proiectului. Am realizat că software-ul trebuie să funcționeze fără probleme pentru a oferi o experiență plăcută utilizatorilor noștri și pentru a ne atinge cu succes obiectivele.
Pentru a asigura această stabilitate, am adoptat o abordare simplă și concentrată asupra esențialului. Am restrâns funcționalitățile aplicației la ceea ce este absolut necesar, eliminând orice element care ar putea introduce complexitate și potențiale puncte de eșec. Acest lucru nu numai că a îmbunătățit fiabilitatea, dar a permis și un proces mai rapid de remediere a erorilor și de implementare a altor funcții în viitor.
Arhitectura simplă a aplicației a servit ca fundație solidă pentru stabilitate și performanță. Am evitat supraîncărcarea cu caracteristici suplimentare și ne-am concentrat pe asigurarea unei experiențe fără cusur pentru utilizatori. Astfel, am fost capabili să ne concentrăm resursele pe îmbunătățirea calității și pe răspunsul rapid la orice problemă care ar putea apărea.
De asemenea, am implementat teste riguroase și procese de verificare pentru a detecta și remedia orice erori sau vulnerabilități înainte ca acestea să afecteze utilizatorii noștri. Această abordare preventivă ne-a permis să menținem o rată scăzută a erorilor și să oferim o experiență de utilizare consistentă și fiabilă.

Versionare:
Echipa noastră a decis să adopte GitHub ca sistem de versionare pentru proiectul nostru. Acest lucru ne permite să urmărim întregul parcurs al dezvoltării aplicatiei prin intermediul repository-ului dedicat. Astfel, fiecare modificare, actualizare sau adăugare la codul sursă poate fi monitorizată și gestionată în mod eficient. GitHub facilitează colaborarea între membrii echipei și contribuie la organizarea și documentarea proiectului într-un mod transparent și accesibil pentru toți cei implicați.


Testarea:
În timpul testării inițiale a aplicației, am ales diverse locații pentru a ne asigura că funcțiile de bază rulează corect. Am efectuat teste în parcuri și în curtea liceului, punând accent pe verificarea corectitudinii funcției de tracking prin GPS și a procesului de cerere a permisiunilor. Prin testarea în aceste medii diferite, am avut oportunitatea să evaluăm comportamentul aplicației în diverse condiții și să identificăm eventuale probleme sau limitări.
Pentru a optimiza experiența utilizatorului și a asigura respectarea cerințelor de securitate și de confidențialitate, am decis să implementăm cererea secvențială a permisiunilor. Aceasta înseamnă că aplicația va solicita utilizatorului aprobarea pentru fiecare permisiune în parte, iar cererea pentru o permisiune ulterioară nu va fi activată până când permisiunea anterioară nu este acordată. Această abordare oferă un control mai mare utilizatorului asupra datelor sale și îmbunătățește transparența în privința modului în care aplicația utilizează informațiile personale.
Pentru a evalua fezabilitatea produsului în mediul său de operare și pentru a testa aplicația în condiții reale, am decis să efectuăm un experiment pe un avion la aerodromul de la Clinceni, care este operat de către Aeroclubul României. Acest mediu oferă oportunitatea de a evalua performanța aplicației într-un context dinamic și de a identifica eventuale probleme sau limitări legate de utilizarea în astfel de condiții. Prin acest experiment, ne propunem să adunăm date și feedback care să ne ajute să îmbunătățim produsul și să asigurăm că acesta poate fi utilizat într-o varietate de situații și medii.
În culisele Aeroclubului României, o nouă fază captivantă începe să prindă contur. Aplicația noastră, care a fost meticulos dezvoltată și rafinată în etapele anterioare, se pregătește acum să-și dovedească valoarea într-un mediu practic și dinamic.
Stadiul final al punerii experimentale în practică marchează un moment crucial în evoluția produsului nostru. Ne propunem să demonstrăm fezabilitatea și funcționalitatea aplicației noastre prin intermediul unei serii de teste pe aeronavele Aeroclubului României, amplasate la aerodromul din Clinceni. Acest pas nu numai că consolidează încrederea în produsul nostru, dar și ne oferă oportunitatea de a obține feedback valoros din partea piloților și a personalului tehnic.
Testarea practică pe teren real este esențială pentru a evalua performanța aplicației noastre în condiții reale de utilizare. De la interfața intuitivă pentru piloți până la funcționalitățile complexe de monitorizare și analiză a datelor de zbor, fiecare aspect al aplicației noastre va fi supus unor teste riguroase și detaliate.
Aerodromul din Clinceni devine astfel terenul de joc pentru echipa noastră și pentru aplicația noastră. În această arenă dinamică, ne propunem să validăm nu doar performanța tehnologică a produsului nostru, ci și capacitatea sa de a aduce valoare reală și tangibilă în operațiunile de zbor.
Pe măsură ce aplicația noastră se pregătește să-și demonstreze fezabilitatea în cadrul Aeroclubului României, suntem nerăbdători să vedem cum va fi primită și să îmbrățișăm oportunitatea de a continua să o dezvoltăm și să o îmbunătățim în funcție de feedback-ul primit din terenul de zbor real.
                 
             
About our teamwork
Munca în echipă a fost un factor esențial care a dus la realizarea ultimei versiuni a aplicației. Am reușit să ne împărțim sarcinile în așa fel încât fiecare dintre noi să se concentreze pe ceea ce știe și poate face cel mai bine, facilitând astfel un proces de dezvoltare fluid și bine coordonat.
Eu, Dunel Octavian, am avut un rol semnificativ în acest proiect, gestionând partea de front-end a aplicației. M-am asigurat că interfața de utilizator este atractivă și intuitivă, iar utilizatorii au o experiență plăcută și ușor de navigat. Pe lângă dezvoltarea designului și funcționalităților interactive, m-am ocupat și de prezentarea aplicației, demonstrând modul în care funcționează și evidențiind avantajele sale principale.
Ideea inițială a aplicației a fost adusă de Neculai Andrei, care s-a ocupat și de partea de backend. Andrei a creat un sistem robust și eficient, asigurându-se că structura de bază și funcționalitățile esențiale sunt solide și bine integrate. Colaborarea noastră strânsă și comunicarea eficientă au permis integrarea perfectă a componentelor back-end și front-end, rezultând într-o aplicație bine realizată și funcțională.
Astfel, sinergia dintre mine și Andrei a fost vitală pentru succesul proiectului. Am demonstrat cum, prin valorificarea punctelor forte ale fiecărui membru al echipei, putem atinge obiective ambițioase și crea produse de înaltă calitate. Munca în echipă a fost cheia acestui succes, și sunt mândru de ceea ce am reușit să realizăm împreună.	

Instalarea aplicatiei Clear2Go:
	Aplicatia poate fi instalată foarte simplu prin descărcarea si rularea apk-ului de pe github aflat la adresa: https://github.com/TheMujdii3/Clear2Go/releases/tag/Judeteana

Acest proiect a fost realizat de către Neculai-Mirea Andrei-Laurențiu și Dunel Ștefan-Octavian, elevi în clasa a X-a A la Colegiul Național Mihai Viteazul din Slobozia, județul Ialomița. Vă mulțumim pentru atenția și timpul acordat parcurgerii acestui document!

Resure:
Reposytory-ul de pe github: https://github.com/TheMujdii3/Clear2Go
Android Studio: https://developer.android.com/studio
Firebase: https://firebase.google.com/
Firebase Analytics: https://firebase.google.com/docs/analytics


