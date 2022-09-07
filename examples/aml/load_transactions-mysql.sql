CREATE DATABASE IF NOT EXISTS `stardog`;

USE `stardog`;

DROP TABLE IF EXISTS `transactions`;

CREATE TABLE `transactions` (
      `tx_id`    INTEGER PRIMARY KEY,
      `account1` VARCHAR(20),
      `account2` VARCHAR(20),
      `amount`   INTEGER,
      `tx_date`  DATE
);

LOAD DATA LOCAL INFILE
'C:/@cyg/demos/aml/raw/aml_dataset_transactions.csv'
INTO TABLE `transactions`
FIELDS TERMINATED BY ','
-- Change line termination to \r\n for Windows
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(`tx_id`,`account1`,`account2`,`amount`,`tx_date`);

CREATE INDEX `account1` ON `transactions` (`account1`);

CREATE INDEX `account2` ON `transactions` (`account2`);

CREATE INDEX `account1_2` ON `transactions` (`account1`, `account2`);

