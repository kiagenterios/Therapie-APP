package com.psych.therapieapp.data.seed

import com.psych.therapieapp.data.local.Converters
import com.psych.therapieapp.data.local.entity.Exercise
import com.psych.therapieapp.data.local.entity.ExerciseCategory

/**
 * Statische, vordefinierte Uebungen fuer die App.
 * Alle Texte auf Deutsch (Selbsthilfe-Tools).
 */
object ExerciseSeeder {

    private val converters = Converters()

    private fun steps(vararg s: String): String = converters.fromStepList(s.toList())

    fun seedExercises(): List<Exercise> = listOf(

        // ---------- Atemtechniken ----------
        Exercise(
            title = "Box-Breathing (4-4-4-4)",
            category = ExerciseCategory.BREATHING,
            description = "Die Box-Atmung beruhigt das Nervensystem und hilft bei Anspannung. " +
                "Vier gleich lange Phasen: einatmen, halten, ausatmen, halten.",
            steps = steps(
                "Setze oder lege dich bequem hin und entspanne die Schultern.",
                "Atme 4 Sekunden lang ruhig durch die Nase ein.",
                "Halte die Luft 4 Sekunden lang an.",
                "Atme 4 Sekunden lang langsam durch den Mund aus.",
                "Halte den Atem 4 Sekunden lang an, bevor du wieder einatmest.",
                "Wiederhole den Zyklus mindestens 4 Mal."
            ),
            durationMinutes = 4
        ),
        Exercise(
            title = "4-7-8-Atmung",
            category = ExerciseCategory.BREATHING,
            description = "Die 4-7-8-Technik nach Dr. Andrew Weil hilft beim Einschlafen und " +
                "bei akuter innerer Unruhe.",
            steps = steps(
                "Lege die Zungenspitze hinter die oberen Schneidezaehne.",
                "Atme komplett durch den Mund aus (mit leisem Rauschen).",
                "Atme 4 Sekunden lang durch die Nase ein.",
                "Halte den Atem 7 Sekunden lang.",
                "Atme 8 Sekunden lang durch den Mund aus.",
                "Wiederhole 4 Zyklen, max. 2x pro Tag in der ersten Woche."
            ),
            durationMinutes = 3
        ),
        Exercise(
            title = "Bauchatmung",
            category = ExerciseCategory.BREATHING,
            description = "Tiefe Zwerchfellatmung gegen Stress und Anspannung.",
            steps = steps(
                "Lege eine Hand auf den Bauch, eine auf die Brust.",
                "Atme so ein, dass sich nur die Hand am Bauch hebt.",
                "Atme langsam und doppelt so lange wieder aus.",
                "Spuere wie der Bauch sich senkt.",
                "5 Minuten ruhig weiteratmen."
            ),
            durationMinutes = 5
        ),

        // ---------- Achtsamkeit ----------
        Exercise(
            title = "Body-Scan",
            category = ExerciseCategory.MINDFULNESS,
            description = "Eine achtsame Reise durch den Koerper, die hilft Anspannung wahrzunehmen " +
                "und loszulassen.",
            steps = steps(
                "Lege dich bequem auf den Ruecken und schliesse die Augen.",
                "Spuere zunaechst deinen gesamten Koerper als Ganzes.",
                "Wandere mit der Aufmerksamkeit zu den Fuessen. Was nimmst du wahr?",
                "Bewege dich langsam nach oben: Waden, Knie, Oberschenkel.",
                "Achte auf Becken, Bauch und unteren Ruecken.",
                "Spuere Brustkorb, Schultern, Arme und Haende.",
                "Beobachte Hals, Gesicht und Kopf ohne zu bewerten.",
                "Spuere zum Abschluss wieder den ganzen Koerper."
            ),
            durationMinutes = 15
        ),
        Exercise(
            title = "5-4-3-2-1-Methode",
            category = ExerciseCategory.MINDFULNESS,
            description = "Grounding-Technik, die bei Panik, Dissoziation oder Reizueberflutung " +
                "schnell zurueck ins Hier-und-Jetzt bringt.",
            steps = steps(
                "Nenne 5 Dinge, die du SEHEN kannst.",
                "Nenne 4 Dinge, die du FUEHLEN kannst (Stoff, Boden, Luft auf der Haut).",
                "Nenne 3 Dinge, die du HOEREN kannst.",
                "Nenne 2 Dinge, die du RIECHEN kannst.",
                "Nenne 1 Sache, die du SCHMECKEN kannst.",
                "Atme tief durch und spuere wie sich dein System beruhigt."
            ),
            durationMinutes = 3
        ),
        Exercise(
            title = "Achtsames Gehen",
            category = ExerciseCategory.MINDFULNESS,
            description = "Bewusstes Gehen als kleine Achtsamkeitspraxis fuer zwischendurch.",
            steps = steps(
                "Stelle dich aufrecht hin und spuere die Fuesse am Boden.",
                "Beginne langsam zu gehen und spuere jeden Schritt.",
                "Achte auf das Abrollen vom Ferse zur Zehe.",
                "Bemerke deinen Atem im Rhythmus deiner Schritte.",
                "Schweifen die Gedanken ab? Kein Problem - sanft zurueck zum Gehen."
            ),
            durationMinutes = 10
        ),

        // ---------- CBT ----------
        Exercise(
            title = "Gedanken-Stop",
            category = ExerciseCategory.CBT,
            description = "Technik um destruktive Gedankenspiralen zu unterbrechen.",
            steps = steps(
                "Bemerke den negativen Gedanken bewusst.",
                "Sage innerlich oder laut: STOPP!",
                "Visualisiere ein rotes Stoppschild.",
                "Atme einmal tief durch.",
                "Lenke deine Aufmerksamkeit aktiv auf etwas Neutrales oder Positives.",
                "Bei Bedarf mehrfach wiederholen - das Gehirn lernt mit jedem Mal."
            ),
            durationMinutes = 2
        ),
        Exercise(
            title = "Gedanken-Check (ABCDE)",
            category = ExerciseCategory.CBT,
            description = "Kognitive Umstrukturierung nach Albert Ellis: Gedanken pruefen statt " +
                "ihnen zu glauben.",
            steps = steps(
                "A - Ausloeser: Was ist passiert? (nur Fakten)",
                "B - Bewertung: Welche Gedanken hattest du dazu?",
                "C - Konsequenz: Welche Gefuehle/Verhalten folgten?",
                "D - Diskussion: Stimmt der Gedanke wirklich? Gibt es Gegenbeweise?",
                "E - Effekt: Welcher hilfreichere Gedanke ist moeglich?",
                "Schreibe den neuen Gedanken auf und lies ihn dir mehrmals durch."
            ),
            durationMinutes = 10
        ),

        // ---------- DBT ----------
        Exercise(
            title = "DBT-Skill: ACCEPTS",
            category = ExerciseCategory.DBT,
            description = "Stressbewaeltigung durch Ablenkung. Jeder Buchstabe ist eine Strategie.",
            steps = steps(
                "A - Aktivitaeten: Tu etwas, das dich beschaeftigt.",
                "C - Contributing: Hilf jemand anderem.",
                "C - Comparisons: Vergleiche dich mit jemandem dem es schlechter geht (vorsichtig).",
                "E - Emotionen: Erzeuge bewusst ein anderes Gefuehl (Musik, Film).",
                "P - Push away: Schiebe das Problem mental beiseite, kurzfristig.",
                "T - Thoughts: Beschaeftige den Kopf (Raetsel, zaehlen, lesen).",
                "S - Sensations: Starke Sinneseindruecke (Eiswuerfel, scharfes Bonbon)."
            ),
            durationMinutes = 5
        ),
        Exercise(
            title = "DBT-Skill: IMPROVE",
            category = ExerciseCategory.DBT,
            description = "Den Moment verbessern, wenn du ihn nicht aendern kannst.",
            steps = steps(
                "I - Imagery: Stelle dir einen sicheren Ort vor.",
                "M - Meaning: Suche Sinn in der Situation.",
                "P - Prayer: Bete oder verbinde dich mit etwas Groesserem.",
                "R - Relaxation: Entspanne deinen Koerper bewusst.",
                "O - One thing in the moment: Konzentriere dich auf nur eine Sache.",
                "V - Vacation: Goenne dir eine Mini-Auszeit (5 Min Tee, kurz raus).",
                "E - Encouragement: Sprich dir selbst Mut zu."
            ),
            durationMinutes = 5
        ),
        Exercise(
            title = "DBT-Skill: Radikale Akzeptanz",
            category = ExerciseCategory.DBT,
            description = "Realitaet annehmen, wie sie ist - ohne zu beschoenigen oder zu verdraengen.",
            steps = steps(
                "Beobachte was gerade ist - ohne Bewertung.",
                "Erinnere dich: die Realitaet ist, wie sie ist - egal ob ich sie mag.",
                "Bedenke: alles hat Ursachen. Die Situation hat einen Grund, auch wenn ich ihn nicht kenne.",
                "Akzeptiere mit dem ganzen Koerper - Schultern locker, Atem ruhig.",
                "Wenn der Widerstand kommt: bemerken, atmen, wieder loslassen.",
                "Du musst es nicht gut finden. Nur akzeptieren, dass es ist."
            ),
            durationMinutes = 8
        ),
        Exercise(
            title = "DBT-Skill: RADIATE / Selbstberuhigung mit den 5 Sinnen",
            category = ExerciseCategory.DBT,
            description = "Selbstberuhigung ueber alle Sinne. Wirkt schnell bei starkem Stress.",
            steps = steps(
                "Sehen: Schau dir etwas Schoenes an (Foto, Pflanze, Himmel).",
                "Hoeren: Lege beruhigende Musik oder Naturgeraeusche auf.",
                "Riechen: Riech an etwas Angenehmem (Tee, Lavendel, Kaffee).",
                "Schmecken: Iss oder trink achtsam etwas, das du magst.",
                "Fuehlen: Kuschle dich in eine Decke, halte etwas Warmes."
            ),
            durationMinutes = 6
        )
    )
}
