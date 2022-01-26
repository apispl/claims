Opis:
Całość logiki przy procesowaniu wniosków bazuje na maszynie stanów. 
Architekturalnie moduł jest zamknięty na korzystanie z metod, z których autor nie chciał, żeby korzystano.
Jedynym publicznym punktem wyjścia jest ClaimFacade, w której zawarta jest cała logika biznesowa.
Testy objeły całość logiki poprzez przetestowanie Fasady.

Założenia
1. Każdemu wnioskowi nadano unikalny identyfikator.
2. Rekordy zmian (HISTORY) zapisuja się w bazie, ale nie jest wystawiony żaden kontroler udostępniajacy je.
3. Aplikacja nie zwraca poprawnych kodów HTTP, tylko 500 i stack z bazy
