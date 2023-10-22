CREATE TABLE `test` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `key` varchar(255) DEFAULT NULL,
    `value` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `test` (`key`, `value`) VALUES ('key', 'value');
