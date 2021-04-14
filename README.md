# eHealth_Anwendung_FHIR

**Einführung**

Ziel diese Projekts ist der Entwurf und die Implementierung einer Digitalen Gesundheitsanwendung, die patientenbezogenen Gesundheitsdaten erfassen und mittels FHIR an einem Server übermitteln kann.
Für einen bestimmten vorhandenen Patient werden mit der Anwendung Gesundheitsziele gesetzt. Der Fortschritt bei der Erreichung der Ziele wird protokolliert. Ziele und Beobachtungen werden für den entsprechenden Patient auf dem Server gespeichert.

**Anforderungen an die Anwendung**


1. Es muss mit der Anwendung möglich sein Patientendaten zu erfassen (der Anwender wird aufgefordert die Daten einzugeben).
2. Folgende Patientendaten sind erforderlich:
    - Vorname, Nachname, Adresse, Geschlecht
3. Es kann einen spezifischen FHIR-Server für den Datenaustausch spezifiziert werden.
4. Ein entsprechender Patient kann mit den angegebenen Daten auf dem spezifizierten Server erstellt werden. Es gibt eine Meldung über den Erfolg/Misserfolg der Patientenerstellung.
5. Es muss möglich sein nach Patienten auf dem Server zu suchen.
    -Gültige Suchkriterien sind mindestens: Vorname, Nachname, Adresse, Geschlecht
6. Es erfolgt eine geeignete Darstellung der gefundenen Patienteninformationen.
7. Für die weitere Arbeit mit der Anwendung, muss es möglich sein den aktuellen Patienten auszuwählen (der Kontext wird gesetzt).
8. Es muss für den Anwender ersichtlich sein welcher der aktuelle Patient ist. 
9. Die Information des ausgewählten Patienten wird ausgegeben.
10. Für den aktuellen Patient muss möglich sein Gesundheitsziele zu setzen. Die Ziele bestehen aus einem numerischen Wert, der entsprechenden Einheit und dem Zeitpunkt der Zielsetzung.
11. Es muss möglich sein die Ziele zu verändern. Den neuen Wert und den neuen Zeitpunkt der Zielsetzung werden protokolliert.
12. Zu den jeweiligen Zielen muss es möglich sein Beobachtungen zu erfassen. Die Beobachtungen bestehen aus einem numerischen Wert, der entsprechenden Einheit und dem Zeitpunkt der Erfassung. Auch erfasst wird wer die Beobachtung durchgeführt hat.
13. Es muss möglich sein für einen bestimmten Patient die definierten Ziele auszugeben.
14. Es muss möglich sein für die Kombination (Patient/Ziel) die erfassten Beobachtungen auszugeben. Die Beobachten werden von einer Beurteilung der Zielerreichung (Ziel erreicht Ja/Nein und ggf. % der Erreichung) begleitet. 

**Weitere Anforderungen/Rahmenbedingungen**

* A. FHIR muss als Kommunikationsstandard für den Datenaustausch zwischen Anwendung und Server verwendet werden. Es soll der aktuelle Standard FHIR v4.0.1: R4 (http://hl7.org/fhir/) verwendet werden. 
* B. Die Digitale Gesundheitsanwendung kann in Form einer Webseite, eines ausführbaren Programms oder eines Jupyter-Notebooks umgesetzt werden.
* C. Es kann eine beliebige Programmiersprache verwendet werden. Es bietet sich an eine Programmiersprache zu verwenden für die es Clients vorhanden sind (C#, Java, javascript, python). 
* D. Bei einer Umsetzung als ausführbares Programm ist eine GUI wünschenswert. Die Interaktion mit dem Anwender kann aber auch über den Terminal erfolgen.
* E. Bei einer Umsetzung als ausführbares Programm, muss das Programm entweder auf Linux, Windows oder Android laufen.
* F. Eine graphische Darstellung der Ergebnisse (z.B. Fortschritt der erfassten Daten mit der Zeit) ist wünschenswert aber nicht zwingend. 
* G. Eine Dokumentation für die Installation, Inbetriebnahme sowie die Bedienung der Digitalen Gesundheitsanwendung ist erforderlich.
* H. Der gesamte Code und die Dokumentation müssen über den Open Project Server der Fakultät Informatik (Hochschule Kempten) zur Verfügung gestellt werden 

**Weitere Ideen (noch mehr Spaß!)**

- Aus den Verschiedenen Zielen und Beobachtungen kann für einen bestimmten Patient ein zusammenfassendes Dokument erstellt werden (FHIR-Ressource „composition“).
- Die Patienten haben einen Arzt/Ärztin und der Leistungserbringer führen einige der Beobachtung durch.
- Das Gerät mit dem die Beobachtungen durchgeführt wurden kann definiert und erfasst werden.
- Leistungserbringer und Geräte können dann als Suchkriterien verwendet werden.
- Man kann nach spezifischen Beobachtungen suchen, die bestimmten Kriterien erfüllen: z.B. das Ziel wurde erreicht bzw. nicht erreicht (oder zu einem bestimmten %-Wert), oder die Beobachtung erfolgte innerhalb eines bestimmten Zeitraums, usw.

**Anmerkung**
Bei Fragen bzw. Unklarheiten bezüglich der Anforderungen, melden Sie sich bitte rechtzeitig!
