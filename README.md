# Job-Assistent 2026
## Intro 
Der Job-Assistent ist ein Desktop-Programm, das die Effizienz bei der Arbeitssuche steigern soll.
Er dient als eine *Anlaufstelle* für diverse Jobportale und erweitert die Filtermöglichkeiten plattformübergreifend.

## Motivation
In der digitalen Welt, in der wir uns heute bewegen, ist ein etablierter Weg Jobportale als primäre Quelle für die 
Jobsuche zu verwenden; in der Regel mehrere gleichzeitig. Hierbei stößt der User auf einen digitalen Wildwuchs, angereichert
mit vielen Ausschreibungen aus Consulting-, Personalvermittlungsfirmen, sowie Fake Vakanzen, verteilt und dupliziert auf 
mehreren Portalen. Erschwerend kommt ein Dickicht aus Login und Registrierungspflichten, der einen Mehraufwand darstellt, den
ich versuche mit diesem Programm zu lindern.

Dabei ist ein Konzept, das ich als *Software as a Servant*, kurz SaaSe, bezeichne ein gedanklicher Wegweiser. Hierbei nehme ich
einen kritischen Blick auf die Trends der letzten 10 Jahre, primär die wachsende Abhängigkeit von Cloud-Services, die unnötige Webisierung
von Applikationen und die damit verbundenen Datenschutzerklärungen, und zeige hoffentlich einen Weg, wie Software ihren alten Stellenwert wieder
erlangen kann: als eine praktische Lösung für alltägliche Probleme.

Gemäß dieser Denke ist mein Programm eine reine Desktop-Applikation ohne Login oder etwaige interne Datenanalytik (mehr dazu weiter unten).

## Vision
Das Projekt besteht aus 3 Phasen. Zu jeder Phase gibt es ein Featureset. Die aktuelle Version 0.0.1 stellt einen MVP (Minimum Viable Product) dar.
In der Gesamtheit dieses Projektes ist das MVP als Phase 1. einzuordnen. Phase 2 stellt ein ausgereiftes Produkt mit bekannten und etablierten Features dar. 
Phase 3 führt KI-Modelle ein. 

## Phase 1
### Architektur
Ziel dieser Projektphase ist ein MVP. Die Wahl des zugrundeliegenden Tech-Stacks wurde unter dem Aspekt der Robustheit sowie einer vielseitigen, 
langjährigen Open-Source Community betrachtet. Ersteres führt unweigerlich zu einer stark typisierten Sprache und zweiteres zu einer Programmiersprache 
mit langer Historie. Es ist ersichtlich, dass diese beiden Angaben mit den Anforderungen von heutigen Enterprise-Architekturen korrelieren; aus meiner 
beruflichen Historie sogar bewusst als Leitlinie verwendet wurden. Die Wahl fiel somit auf Java; genauer gesagt JavaFX für das Frontend und Java für das 
Backend. Es wurde bewusst Wert auf ein homogenes Ökosystem gelegt, da moderne Frameworks wie React oder Angular einen zu kurzen Lebenszyklus aufweisen sowie 
einen größere Overhead für Deployments im Cloud-Bereich darstellen. Gleichermaßen stehen neueste Java Applikationen durch den Reifeprozess, den die Sprache 
in den letzten Jahren erfahren durfte, in keinster Weise nachteilig gegenüber Cloud-Umgebungen dar, sodass dieses Projekt auch in Zukunft auf eine Cloud-Umgebung 
umgestellt werden kann bzw. der Entwicklungsprozess immer ein diese Spezifikation im Blick behält.

Innerhalb des Projektes wurde ein großer Wert auf Clean-Code sowie Design Patterns gelegt, wobei besonders beim MVP darauf geachtet wurde kein "Over-Engineering" 
zu betreiben. Praktisch bedeutet es, dass _Singletons_ verwendet wurde, die in Phase 2 zu _Factories_ umgebaut werden; gleichsam wurden rudimentäre _Alerts_ eingebaut, 
die im späteren Verlauf einerseits stärker _decoupled_ werden und andererseits mittels einer eventbasierten Architektur umgesetzt werden. Insgesamt wurde ein _MVC_ (Model View Controller)
Konzept verwendet, welches sich schon auf mobilen Plattformen wie iOS oder Kotlin durchgesetzt hat. 

### Featureset
Das MVP beinhaltet eine grundlegende UI, die es ermöglicht eine Suche bei Stepstone anzulegen und die gefundenen Stellen darzustellen. Das System arbeitet mit einer lokalen Datenbank, die alle
Stellenausschreibungen sowie den dazugehörigen Unternehmen speichert. Zusätzlich wird ein Link der internen Stellenbörse angezeigt. Der User kann ungewollte Unternehmen direkt aus der Anzeige banne, sodass
zukünftig keine Stellen von diesem Unternehmen angezeigt werden. 

### Installation
Das Projekt muss heruntergeladen werden und im Terminal mittels `mvn javafx:run` gestartet werden. Voraussetzung ist hierbei Java, Javafx sowie Maven.
Ein Wizzard ist in Phase 2 geplant. 


## Work in Progress...

