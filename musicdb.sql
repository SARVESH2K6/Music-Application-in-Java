-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 31, 2024 at 05:39 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `musicdb`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_song_to_playlist` (IN `playlistName` VARCHAR(255), IN `userName` VARCHAR(255), IN `songName` VARCHAR(255), IN `albumName` VARCHAR(255), IN `albumLanguage` VARCHAR(255))   BEGIN
    DECLARE playlist_id INT;
    DECLARE song_id INT;
    DECLARE user_id INT;

    -- Get the playlist_id and user_id
    SELECT p.id, u.id INTO playlist_id, user_id
    FROM playlists p
    JOIN users u ON p.user_id = u.id
    WHERE p.name = playlistName AND u.name = userName;

    -- Get the song_id
    SELECT s.id INTO song_id
    FROM songs s
    JOIN albums a ON s.album_id = a.id
    WHERE s.name = songName AND a.name = albumName AND s.language = albumLanguage;

    -- Insert into playlist_songs
    INSERT INTO playlist_songs (playlist_id, user_id, song_id)
    VALUES (playlist_id, user_id, song_id);
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `albums`
--

CREATE TABLE `albums` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `language` varchar(50) NOT NULL,
  `average_rating` float DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `albums`
--

INSERT INTO `albums` (`id`, `name`, `language`, `average_rating`) VALUES
(1, 'Jailer', 'Tamil', 2.5),
(2, 'Leo', 'Tamil', 0),
(3, 'Master', 'Tamil', NULL),
(4, 'RRR', 'Telugu', NULL),
(5, 'Baahubali 2', 'Telugu', NULL),
(6, 'Sye Raa Narasimha Reddy', 'Telugu', NULL),
(7, 'Pulimurugan', 'Malayalam', NULL),
(8, 'Kumbalangi Nights', 'Malayalam', NULL),
(9, 'Drishyam 2', 'Malayalam', NULL),
(10, 'KGF Chapter 2', 'Kannada', NULL),
(11, 'KGF Chapter 1', 'Kannada', NULL),
(12, 'Roberrt', 'Kannada', NULL),
(13, 'Pathaan', 'Hindi', 2.5),
(14, 'Bajrangi Bhaijaan', 'Hindi', NULL),
(15, 'Dangal', 'Hindi', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `playlists`
--

CREATE TABLE `playlists` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `playlists`
--

INSERT INTO `playlists` (`id`, `name`, `user_id`) VALUES
(1, 'Charger', 1);

-- --------------------------------------------------------

--
-- Table structure for table `playlist_songs`
--

CREATE TABLE `playlist_songs` (
  `id` int(11) NOT NULL,
  `playlist_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `song_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `playlist_songs`
--

INSERT INTO `playlist_songs` (`id`, `playlist_id`, `user_id`, `song_id`) VALUES
(2, 1, 1, 3),
(3, 1, 1, 2);

--
-- Triggers `playlist_songs`
--
DELIMITER $$
CREATE TRIGGER `increment_play_count` AFTER INSERT ON `playlist_songs` FOR EACH ROW BEGIN
    UPDATE songs
    SET play_count = play_count + 1
    WHERE id = NEW.song_id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `songs`
--

CREATE TABLE `songs` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `artist` varchar(100) DEFAULT NULL,
  `album_id` int(11) DEFAULT NULL,
  `language` varchar(50) NOT NULL,
  `play_count` int(11) DEFAULT 0,
  `rating` float DEFAULT 0,
  `rating_count` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `songs`
--

INSERT INTO `songs` (`id`, `name`, `artist`, `album_id`, `language`, `play_count`, `rating`, `rating_count`) VALUES
(1, 'Kaavaala', 'Shilpa Rao', 1, 'Tamil', 0, 0, NULL),
(2, 'Hukum', 'Anirudh Ravichander', 1, 'Tamil', 11, 5, NULL),
(3, 'Naa Ready', 'Vijay', 2, 'Tamil', 11, 0, NULL),
(4, 'Badass', 'Anirudh Ravichander', 2, 'Tamil', 0, 0, NULL),
(5, 'Kutti Story', 'Vijay', 3, 'Tamil', 0, 0, NULL),
(6, 'Vaathi Coming', 'Anirudh Ravichander', 3, 'Tamil', 0, 0, NULL),
(7, 'Naatu Naatu', 'Rahul Sipligunj', 4, 'Telugu', 0, 0, NULL),
(8, 'Komuram Bheem', 'Kaala Bhairava', 4, 'Telugu', 0, 0, NULL),
(9, 'Saahore Baahubali', 'Ramya Behara', 5, 'Telugu', 0, 0, NULL),
(10, 'Pakka Local', 'M. M. Kreem', 5, 'Telugu', 0, 0, NULL),
(11, 'Jana Gana Mana', 'Chiranjeevi', 6, 'Telugu', 0, 0, NULL),
(12, 'Aye Raju', 'Shreya Ghoshal', 6, 'Telugu', 0, 0, NULL),
(13, 'Maanathe Makal', 'Vineeth Sreenivasan', 7, 'Malayalam', 0, 0, NULL),
(14, 'Punnamada', 'S. P. Balasubrahmanyam', 7, 'Malayalam', 0, 0, NULL),
(15, 'Entammede Jimikki Kammal', 'Vineeth Sreenivasan', 8, 'Malayalam', 0, 0, NULL),
(16, 'Kumbalangi Nights', 'Shan Rahman', 8, 'Malayalam', 0, 0, NULL),
(17, 'Pathinettu', 'Najeem Arshad', 9, 'Malayalam', 0, 0, NULL),
(18, 'Dheem Tharikida', 'K.J. Yesudas', 9, 'Malayalam', 0, 0, NULL),
(19, 'Toofan', 'Vijay Prakash', 10, 'Kannada', 0, 0, NULL),
(20, 'Yash', 'Yash', 10, 'Kannada', 0, 0, NULL),
(21, 'Gali Gali', 'Rajesh Krishnan', 11, 'Kannada', 0, 0, NULL),
(22, 'Sultan', 'Vijay Prakash', 11, 'Kannada', 0, 0, NULL),
(23, 'Duniya', 'Armaan Malik', 12, 'Kannada', 0, 0, NULL),
(24, 'Eagle', 'Chaitra H. G.', 12, 'Kannada', 0, 0, NULL),
(25, 'Jhoome Jo Pathaan', 'Arijit Singh', 13, 'Hindi', 5, 5, NULL),
(26, 'Besharam Rang', 'Shreya Ghoshal', 13, 'Hindi', 0, 0, NULL),
(27, 'Tera Ban Jaunga', 'Akhil Sachdeva', 14, 'Hindi', 0, 0, NULL),
(28, 'Tu Jo Mila', 'Kamaal Khan', 14, 'Hindi', 0, 0, NULL),
(29, 'Naina', 'Arijit Singh', 15, 'Hindi', 0, 0, NULL),
(30, 'Haanikaarak Bapu', 'Siddharth Mahadevan', 15, 'Hindi', 0, 0, NULL);

--
-- Triggers `songs`
--
DELIMITER $$
CREATE TRIGGER `update_album_rating` AFTER UPDATE ON `songs` FOR EACH ROW BEGIN
    DECLARE avg_rating FLOAT;
    
    -- Calculate the average rating of all songs in the album
    SELECT AVG(rating) INTO avg_rating
    FROM songs
    WHERE album_id = NEW.album_id;

    -- Update the album's average rating
    UPDATE albums
    SET average_rating = avg_rating
    WHERE id = NEW.album_id;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `password`) VALUES
(1, 'Sarvesh', '203121');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `albums`
--
ALTER TABLE `albums`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `playlists`
--
ALTER TABLE `playlists`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `playlist_songs`
--
ALTER TABLE `playlist_songs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `playlist_id` (`playlist_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `song_id` (`song_id`);

--
-- Indexes for table `songs`
--
ALTER TABLE `songs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `album_id` (`album_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `albums`
--
ALTER TABLE `albums`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `playlists`
--
ALTER TABLE `playlists`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `playlist_songs`
--
ALTER TABLE `playlist_songs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `songs`
--
ALTER TABLE `songs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `playlists`
--
ALTER TABLE `playlists`
  ADD CONSTRAINT `playlists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `playlist_songs`
--
ALTER TABLE `playlist_songs`
  ADD CONSTRAINT `playlist_songs_ibfk_1` FOREIGN KEY (`playlist_id`) REFERENCES `playlists` (`id`),
  ADD CONSTRAINT `playlist_songs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `playlist_songs_ibfk_3` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`);

--
-- Constraints for table `songs`
--
ALTER TABLE `songs`
  ADD CONSTRAINT `songs_ibfk_1` FOREIGN KEY (`album_id`) REFERENCES `albums` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
