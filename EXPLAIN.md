## SQL-запрос

select * from documents where status = 'DRAFT';

## Explain Analyze

        Seq Scan on documents  (cost=0.00..31.45 rows=1136 width=80) (actual time=0.020..0.189 rows=1121 loops=1)
        Filter: (status = 'DRAFT'::document_status)
        Rows Removed by Filter: 115
        Planning Time: 0.054 ms
        Execution Time: 0.237 ms
        (5 rows)

## Анализ 

Исопльзуется Seq Scan, так как при большом объёме искомых данных планировщик делает вывод,
что быстрее и выгоднее будет использовать построчный проход по таблице, чем B-tree индекс.
Временные затраты на проход индексного дерева при большом количестве данных будут 
значительно выше чем на последовтальный проход.

## Вывод 

Стоит добавить индекс на колонку "status" таблицы documents для небольших выборок.

----------------------------------------------------------------------------------------------------------------------

## SQL-запрос

SELECT d.*, h.* FROM documents d LEFT JOIN history h ON h.document_id = d.id WHERE d.id = :2;

## Explain Analyze

        Nested Loop Left Join  (cost=0.28..13.00 rows=2 width=632) (actual time=0.028..0.042 rows=2 loops=1)
        ->  Index Scan using documents_pkey on documents d  (cost=0.28..8.29 rows=1 width=80) (actual time=0.018..0.019 
        rows=1 loops=1)
        Index Cond: (id = 2)
        ->  Seq Scan on history h  (cost=0.00..4.69 rows=2 width=552) (actual time=0.009..0.021 rows=2 loops=1)
        Filter: (document_id = 2)
        Rows Removed by Filter: 223
        Planning Time: 0.101 ms
        Execution Time: 0.111 ms
        (8 rows)


## Анализ

Для таблицы history используется последовательное сканирование (Seq Scan), так как отсутствует 
индекс по полю document_id. При текущем объёме данных это не оказывает существенного влияния на производительность. 
Однако при росте таблицы возможна деградация, поскольку для каждого найденного документа будет выполняться полный 
перебор таблицы history.

## Вывод

Стоит добавить индекс на колонку "document_id" таблицы history.

----------------------------------------------------------------------------------------------------------------------
