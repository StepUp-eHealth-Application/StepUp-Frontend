# /UC5/ Patienten auf dem Server suchen
## Übersicht
  |||
 ---------------|---------------------------------------------------------------
  Ziel          | Finden eines oder mehrerer Patienten
  Vorbedingung  | Der Nutzer hat die App gestartet
  Resultat      | Eine Liste aller auf die Suche passender Patienten wird angezeigt
  Nutzer        | Arzt
  Auslöser      | Entertaste wird innerhalb der Suchzeile gedrückt
  ------------------------------------------------------------------------------
## Detailbeschreibung
**Kurzbeschreibung**: Ein Arzt kann nach bestimmten Patienten auf dem Server über verschiedene Kriterien suchen.
**Akteure**:
* Arzt
* FHIR Server
**Eingehende Daten**:
* Vorname
* Nachname
* Adresse
* Geschlecht
**Essentielle Schritte**: 
1. Arzt klickt auf die Suchzeile
2. Arzt gibt ein Suchkriterium ein und bestätigt die Suche mit der Entertaste
3. Eine Liste aller zutreffenden Patienten wird angezeigt
**Ausnahmen**:
* zu 3.) gibt es keine Patienten auf welche die Sucheingabe zutrifft wird statt der Liste die Nachricht "Keine Patienten gefunden" angezeigt
**Änderungshistorie**:
* 22.04.2021; Daniel Heiserer; Use Case angelegt

