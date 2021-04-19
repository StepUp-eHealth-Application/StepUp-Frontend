# /UC9/ Informationen des aktuellen Patienten ausgeben

## Übersicht

  |||
 ---------------|---------------------------------------------------------------
  Ziel          | Informationen zum aktuellen Patienten werden angezeigt.
  Vorbedingung  | Der Nutzer hat die App gestartet und einen beliebigen Patienten ausgewählt.
  Resultat      | Informationen zum ausgewählten Patient werden auf dem Bildschirm angezeigt.
  Nutzer        | Arzt
  Auslöser      | Button "Alle Informationen zum Patient anzeigen" gedrückt
  ------------------------------------------------------------------------------

## Detailbeschreibung

**Kurzbeschreibung**: Ein Arzt kann sich Informationen zu einem vorher ausgewählten Patienten anzeigen lassen.

**Akteure**:
* Arzt
* FHIR Server

**Eingehende Daten**:

**Essentielle Schritte**: 
1. Arzt hat einen Patient ausgewählt
2. Arzt klickt "alle Informationen anzeigen" an
3. Alle verfügbaren Informationen zum Patient werden auf dem Bildschirm ausgegeben

**Ausnahmen**:


**Änderungshistorie**:
* 19.04.2021; Jonathan Hilscher; Use Case angelegt
