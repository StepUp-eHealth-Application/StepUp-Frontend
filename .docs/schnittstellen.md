# Preferences
- Preferenz um eigene Patienten ID abzuspeichern (Name: PATIENT_ID, String)
- Preferenz um ausgewählten Patienten ID abzuspeichern (Name: SELECTED_PATIENT_ID, String)
- FHIR Server URL wird in einer Preferenz abgespeichert (Name: FHIR_SERVER_URL, String)

# Activities
- Einstellungen werden auf jeder Activity angezeigt
- Rollenauswahl -> Hauptmenü (Falls Patient), Patient suchen (Falls Leistungserbringer)
- Patient suchen -> Hauptmenü
- Hauptmenü -> Einstellungen, Zusammenfassungen erstellen, Beobachtungen erfassen, Gesundheitsziele anzeigen, Patientendaten anzeigen, Patientendaten eingeben, Beobachtungen anzeigen, Gesundheitsziele setzen, Diagnosegerät erstellen
- Gesundheitsziele anzeigen -> Gesundheitsziele ändern (Gleiche Activity wie Gesundheitsziele setzen)

# Datenaustausch zwischen Activities
- Option 1: Wechsel zu Gesundheitsziele ändern, diese Daten müssen mitgegeben werden:#
    - ID des Gesundheitsziel weiter geben (Key: HEALTH_GOAL_ID)
    - ggf. Name des Gesundheitsziels (Key: HEALTH_GOAL_NAME)
    - geplante Schritte bzw. geplantes Zielgewicht (Key: HEALTH_GOAL_VALUE)
    - Datum (Key: HEALTH_GOAL_DATE)
- Option 2: Wechsel zu Gesundheitsziele ändern, diese Daten müssen mitgegeben werden:
    - ID des Gesundheitsziel weiter geben (Key: HEALTH_GOAL_ID)
    - Daten anhand der ID vom FHIR Server anfragen