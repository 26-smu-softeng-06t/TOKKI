-- Development seed for issue #31: 3 difficulties x 10 stages x 10 words.
-- Source: C:/Users/loner/Downloads/word.sql, normalized to the TOKKI schema.

INSERT INTO stages (difficulty, stage_number, level, created_at, title, description)
VALUES
    ('easy', 1, 1, NOW(6), 'Easy Stage 1', 'Easy level - Stage 1'),
    ('easy', 2, 2, NOW(6), 'Easy Stage 2', 'Easy level - Stage 2'),
    ('easy', 3, 3, NOW(6), 'Easy Stage 3', 'Easy level - Stage 3'),
    ('easy', 4, 4, NOW(6), 'Easy Stage 4', 'Easy level - Stage 4'),
    ('easy', 5, 5, NOW(6), 'Easy Stage 5', 'Easy level - Stage 5'),
    ('easy', 6, 6, NOW(6), 'Easy Stage 6', 'Easy level - Stage 6'),
    ('easy', 7, 7, NOW(6), 'Easy Stage 7', 'Easy level - Stage 7'),
    ('easy', 8, 8, NOW(6), 'Easy Stage 8', 'Easy level - Stage 8'),
    ('easy', 9, 9, NOW(6), 'Easy Stage 9', 'Easy level - Stage 9'),
    ('easy', 10, 10, NOW(6), 'Easy Stage 10', 'Easy level - Stage 10'),
    ('medium', 1, 1, NOW(6), 'Medium Stage 1', 'Medium level - Stage 1'),
    ('medium', 2, 2, NOW(6), 'Medium Stage 2', 'Medium level - Stage 2'),
    ('medium', 3, 3, NOW(6), 'Medium Stage 3', 'Medium level - Stage 3'),
    ('medium', 4, 4, NOW(6), 'Medium Stage 4', 'Medium level - Stage 4'),
    ('medium', 5, 5, NOW(6), 'Medium Stage 5', 'Medium level - Stage 5'),
    ('medium', 6, 6, NOW(6), 'Medium Stage 6', 'Medium level - Stage 6'),
    ('medium', 7, 7, NOW(6), 'Medium Stage 7', 'Medium level - Stage 7'),
    ('medium', 8, 8, NOW(6), 'Medium Stage 8', 'Medium level - Stage 8'),
    ('medium', 9, 9, NOW(6), 'Medium Stage 9', 'Medium level - Stage 9'),
    ('medium', 10, 10, NOW(6), 'Medium Stage 10', 'Medium level - Stage 10'),
    ('hard', 1, 1, NOW(6), 'Hard Stage 1', 'Hard level - Stage 1'),
    ('hard', 2, 2, NOW(6), 'Hard Stage 2', 'Hard level - Stage 2'),
    ('hard', 3, 3, NOW(6), 'Hard Stage 3', 'Hard level - Stage 3'),
    ('hard', 4, 4, NOW(6), 'Hard Stage 4', 'Hard level - Stage 4'),
    ('hard', 5, 5, NOW(6), 'Hard Stage 5', 'Hard level - Stage 5'),
    ('hard', 6, 6, NOW(6), 'Hard Stage 6', 'Hard level - Stage 6'),
    ('hard', 7, 7, NOW(6), 'Hard Stage 7', 'Hard level - Stage 7'),
    ('hard', 8, 8, NOW(6), 'Hard Stage 8', 'Hard level - Stage 8'),
    ('hard', 9, 9, NOW(6), 'Hard Stage 9', 'Hard level - Stage 9'),
    ('hard', 10, 10, NOW(6), 'Hard Stage 10', 'Hard level - Stage 10')
ON DUPLICATE KEY UPDATE
    level = VALUES(level),
    title = VALUES(title),
    description = VALUES(description);

-- Easy Stage_1
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 1;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Apple' AS word, '사과' AS meaning, 'I eat an apple.' AS example, 1 AS ord UNION ALL
    SELECT 'Banana' AS word, '바나나' AS meaning, 'Yellow banana.' AS example, 2 AS ord UNION ALL
    SELECT 'Cake' AS word, '케이크' AS meaning, 'Sweet cake.' AS example, 3 AS ord UNION ALL
    SELECT 'Desk' AS word, '책상' AS meaning, 'On the desk.' AS example, 4 AS ord UNION ALL
    SELECT 'Ear' AS word, '귀' AS meaning, 'Listen with ears.' AS example, 5 AS ord UNION ALL
    SELECT 'Frog' AS word, '개구리' AS meaning, 'Green frog.' AS example, 6 AS ord UNION ALL
    SELECT 'Game' AS word, '게임' AS meaning, 'Play a game.' AS example, 7 AS ord UNION ALL
    SELECT 'Hand' AS word, '손' AS meaning, 'Wash your hands.' AS example, 8 AS ord UNION ALL
    SELECT 'Ink' AS word, '잉크' AS meaning, 'Blue ink.' AS example, 9 AS ord UNION ALL
    SELECT 'Juice' AS word, '주스' AS meaning, 'Orange juice.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 1;

-- Easy Stage_2
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 2;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'School' AS word, '학교' AS meaning, 'I go to school.' AS example, 1 AS ord UNION ALL
    SELECT 'Teacher' AS word, '선생님' AS meaning, 'Our teacher is kind.' AS example, 2 AS ord UNION ALL
    SELECT 'Student' AS word, '학생' AS meaning, 'I am a student.' AS example, 3 AS ord UNION ALL
    SELECT 'Pencil' AS word, '연필' AS meaning, 'Write with a pencil.' AS example, 4 AS ord UNION ALL
    SELECT 'Eraser' AS word, '지우개' AS meaning, 'Use an eraser.' AS example, 5 AS ord UNION ALL
    SELECT 'Paper' AS word, '종이' AS meaning, 'Write on paper.' AS example, 6 AS ord UNION ALL
    SELECT 'Lesson' AS word, '수업' AS meaning, 'Music lesson.' AS example, 7 AS ord UNION ALL
    SELECT 'Study' AS word, '공부하다' AS meaning, 'Study hard.' AS example, 8 AS ord UNION ALL
    SELECT 'Exam' AS word, '시험' AS meaning, 'Final exam.' AS example, 9 AS ord UNION ALL
    SELECT 'Grade' AS word, '성적' AS meaning, 'Good grades.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 2;

-- Easy Stage_3
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 3;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'House' AS word, '집' AS meaning, 'My house is small.' AS example, 1 AS ord UNION ALL
    SELECT 'Room' AS word, '방' AS meaning, 'Clean your room.' AS example, 2 AS ord UNION ALL
    SELECT 'Door' AS word, '문' AS meaning, 'Open the door.' AS example, 3 AS ord UNION ALL
    SELECT 'Window' AS word, '창문' AS meaning, 'Look out the window.' AS example, 4 AS ord UNION ALL
    SELECT 'Chair' AS word, '의자' AS meaning, 'Sit on the chair.' AS example, 5 AS ord UNION ALL
    SELECT 'Table' AS word, '탁자' AS meaning, 'On the table.' AS example, 6 AS ord UNION ALL
    SELECT 'Bed' AS word, '침대' AS meaning, 'Sleep in bed.' AS example, 7 AS ord UNION ALL
    SELECT 'Kitchen' AS word, '주방' AS meaning, 'Cook in the kitchen.' AS example, 8 AS ord UNION ALL
    SELECT 'Floor' AS word, '바닥' AS meaning, 'Wooden floor.' AS example, 9 AS ord UNION ALL
    SELECT 'Wall' AS word, '벽' AS meaning, 'White wall.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 3;

-- Easy Stage_4
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 4;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Bread' AS word, '빵' AS meaning, 'Eat fresh bread.' AS example, 1 AS ord UNION ALL
    SELECT 'Milk' AS word, '우유' AS meaning, 'Drink cold milk.' AS example, 2 AS ord UNION ALL
    SELECT 'Water' AS word, '물' AS meaning, 'Fresh water.' AS example, 3 AS ord UNION ALL
    SELECT 'Rice' AS word, '쌀, 밥' AS meaning, 'Cook rice.' AS example, 4 AS ord UNION ALL
    SELECT 'Fruit' AS word, '과일' AS meaning, 'Sweet fruit.' AS example, 5 AS ord UNION ALL
    SELECT 'Grape' AS word, '포도' AS meaning, 'Purple grapes.' AS example, 6 AS ord UNION ALL
    SELECT 'Orange' AS word, '오렌지' AS meaning, 'Peel an orange.' AS example, 7 AS ord UNION ALL
    SELECT 'Pizza' AS word, '피자' AS meaning, 'Hot pizza.' AS example, 8 AS ord UNION ALL
    SELECT 'Soup' AS word, '수프' AS meaning, 'Tomato soup.' AS example, 9 AS ord UNION ALL
    SELECT 'Sugar' AS word, '설탕' AS meaning, 'Add sugar.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 4;

-- Easy Stage_5
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 5;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Head' AS word, '머리' AS meaning, 'Nod your head.' AS example, 1 AS ord UNION ALL
    SELECT 'Face' AS word, '얼굴' AS meaning, 'Wash your face.' AS example, 2 AS ord UNION ALL
    SELECT 'Eye' AS word, '눈' AS meaning, 'Blue eyes.' AS example, 3 AS ord UNION ALL
    SELECT 'Nose' AS word, '코' AS meaning, 'Long nose.' AS example, 4 AS ord UNION ALL
    SELECT 'Mouth' AS word, '입' AS meaning, 'Open your mouth.' AS example, 5 AS ord UNION ALL
    SELECT 'Arm' AS word, '팔' AS meaning, 'Strong arm.' AS example, 6 AS ord UNION ALL
    SELECT 'Leg' AS word, '다리' AS meaning, 'Fast legs.' AS example, 7 AS ord UNION ALL
    SELECT 'Foot' AS word, '발' AS meaning, 'Left foot.' AS example, 8 AS ord UNION ALL
    SELECT 'Hair' AS word, '머리카락' AS meaning, 'Black hair.' AS example, 9 AS ord UNION ALL
    SELECT 'Tooth' AS word, '치아' AS meaning, 'Brush your teeth.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 5;

-- Easy Stage_6
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 6;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Sun' AS word, '해' AS meaning, 'The sun is hot.' AS example, 1 AS ord UNION ALL
    SELECT 'Rain' AS word, '비' AS meaning, 'Rainy day.' AS example, 2 AS ord UNION ALL
    SELECT 'Cloud' AS word, '구름' AS meaning, 'White cloud.' AS example, 3 AS ord UNION ALL
    SELECT 'Snow' AS word, '눈' AS meaning, 'Cold snow.' AS example, 4 AS ord UNION ALL
    SELECT 'Wind' AS word, '바람' AS meaning, 'Strong wind.' AS example, 5 AS ord UNION ALL
    SELECT 'Star' AS word, '별' AS meaning, 'Bright star.' AS example, 6 AS ord UNION ALL
    SELECT 'Sky' AS word, '하늘' AS meaning, 'Blue sky.' AS example, 7 AS ord UNION ALL
    SELECT 'Cold' AS word, '추운' AS meaning, 'It is cold.' AS example, 8 AS ord UNION ALL
    SELECT 'Hot' AS word, '뜨거운' AS meaning, 'Hot summer.' AS example, 9 AS ord UNION ALL
    SELECT 'Dark' AS word, '어두운' AS meaning, 'It is dark.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 6;

-- Easy Stage_7
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 7;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Lion' AS word, '사자' AS meaning, 'King of the jungle.' AS example, 1 AS ord UNION ALL
    SELECT 'Tiger' AS word, '호랑이' AS meaning, 'Fast tiger.' AS example, 2 AS ord UNION ALL
    SELECT 'Bird' AS word, '새' AS meaning, 'Bird flies.' AS example, 3 AS ord UNION ALL
    SELECT 'Cow' AS word, '소' AS meaning, 'Cow gives milk.' AS example, 4 AS ord UNION ALL
    SELECT 'Pig' AS word, '돼지' AS meaning, 'Pink pig.' AS example, 5 AS ord UNION ALL
    SELECT 'Duck' AS word, '오리' AS meaning, 'Duck swims.' AS example, 6 AS ord UNION ALL
    SELECT 'Bear' AS word, '곰' AS meaning, 'Brown bear.' AS example, 7 AS ord UNION ALL
    SELECT 'Mouse' AS word, '쥐' AS meaning, 'Small mouse.' AS example, 8 AS ord UNION ALL
    SELECT 'Rabbit' AS word, '토끼' AS meaning, 'White rabbit.' AS example, 9 AS ord UNION ALL
    SELECT 'Horse' AS word, '말' AS meaning, 'Ride a horse.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 7;

-- Easy Stage_8
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 8;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Car' AS word, '차' AS meaning, 'Drive a car.' AS example, 1 AS ord UNION ALL
    SELECT 'Bus' AS word, '버스' AS meaning, 'Take the bus.' AS example, 2 AS ord UNION ALL
    SELECT 'Train' AS word, '기차' AS meaning, 'Fast train.' AS example, 3 AS ord UNION ALL
    SELECT 'Bike' AS word, '자전거' AS meaning, 'Ride a bike.' AS example, 4 AS ord UNION ALL
    SELECT 'Plane' AS word, '비행기' AS meaning, 'Fly a plane.' AS example, 5 AS ord UNION ALL
    SELECT 'Ship' AS word, '배' AS meaning, 'Large ship.' AS example, 6 AS ord UNION ALL
    SELECT 'Truck' AS word, '트럭' AS meaning, 'Heavy truck.' AS example, 7 AS ord UNION ALL
    SELECT 'Taxi' AS word, '택시' AS meaning, 'Call a taxi.' AS example, 8 AS ord UNION ALL
    SELECT 'Subway' AS word, '지하철' AS meaning, 'Use the subway.' AS example, 9 AS ord UNION ALL
    SELECT 'Boat' AS word, '보트' AS meaning, 'Small boat.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 8;

-- Easy Stage_9
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 9;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Red' AS word, '빨간색' AS meaning, 'Red apple.' AS example, 1 AS ord UNION ALL
    SELECT 'Blue' AS word, '파란색' AS meaning, 'Blue sea.' AS example, 2 AS ord UNION ALL
    SELECT 'Green' AS word, '초록색' AS meaning, 'Green grass.' AS example, 3 AS ord UNION ALL
    SELECT 'Yellow' AS word, '노란색' AS meaning, 'Yellow sun.' AS example, 4 AS ord UNION ALL
    SELECT 'Black' AS word, '검은색' AS meaning, 'Black cat.' AS example, 5 AS ord UNION ALL
    SELECT 'White' AS word, '하얀색' AS meaning, 'White paper.' AS example, 6 AS ord UNION ALL
    SELECT 'Circle' AS word, '원' AS meaning, 'Draw a circle.' AS example, 7 AS ord UNION ALL
    SELECT 'Square' AS word, '사각형' AS meaning, 'Square box.' AS example, 8 AS ord UNION ALL
    SELECT 'Color' AS word, '색깔' AS meaning, 'Favorite color.' AS example, 9 AS ord UNION ALL
    SELECT 'Bright' AS word, '밝은' AS meaning, 'Bright light.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 9;

-- Easy Stage_10
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'easy' AND s.stage_number = 10;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Happy' AS word, '행복한' AS meaning, 'I am happy.' AS example, 1 AS ord UNION ALL
    SELECT 'Sad' AS word, '슬픈' AS meaning, 'Dont be sad.' AS example, 2 AS ord UNION ALL
    SELECT 'Smile' AS word, '미소' AS meaning, 'Beautiful smile.' AS example, 3 AS ord UNION ALL
    SELECT 'Laugh' AS word, '웃다' AS meaning, 'Laugh out loud.' AS example, 4 AS ord UNION ALL
    SELECT 'Angry' AS word, '화난' AS meaning, 'Angry face.' AS example, 5 AS ord UNION ALL
    SELECT 'Sleep' AS word, '자다' AS meaning, 'Time to sleep.' AS example, 6 AS ord UNION ALL
    SELECT 'Eat' AS word, '먹다' AS meaning, 'Eat lunch.' AS example, 7 AS ord UNION ALL
    SELECT 'Drink' AS word, '마시다' AS meaning, 'Drink water.' AS example, 8 AS ord UNION ALL
    SELECT 'Run' AS word, '달리다' AS meaning, 'Run fast.' AS example, 9 AS ord UNION ALL
    SELECT 'Walk' AS word, '걷다' AS meaning, 'Walk home.' AS example, 10 AS ord
) t WHERE s.difficulty = 'easy' AND s.stage_number = 10;

-- medium stage_1
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 1;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Abandon' AS word, '포기하다' AS meaning, 'Never abandon hope.' AS example, 1 AS ord UNION ALL
    SELECT 'Believe' AS word, '믿다' AS meaning, 'I believe you.' AS example, 2 AS ord UNION ALL
    SELECT 'Comfort' AS word, '편안함' AS meaning, 'Home comfort.' AS example, 3 AS ord UNION ALL
    SELECT 'Danger' AS word, '위험' AS meaning, 'Avoid danger.' AS example, 4 AS ord UNION ALL
    SELECT 'Effect' AS word, '효과' AS meaning, 'Side effect.' AS example, 5 AS ord UNION ALL
    SELECT 'Famous' AS word, '유명한' AS meaning, 'A famous actor.' AS example, 6 AS ord UNION ALL
    SELECT 'Gentle' AS word, '온화한' AS meaning, 'Gentle breeze.' AS example, 7 AS ord UNION ALL
    SELECT 'Happen' AS word, '일어나다' AS meaning, 'What happened?' AS example, 8 AS ord UNION ALL
    SELECT 'Ignore' AS word, '무시하다' AS meaning, 'Ignore the noise.' AS example, 9 AS ord UNION ALL
    SELECT 'Junior' AS word, '후배' AS meaning, 'Junior staff.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 1;

-- medium stage_2
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 2;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Anxiety' AS word, '불안' AS meaning, 'Dealing with anxiety.' AS example, 1 AS ord UNION ALL
    SELECT 'Confidence' AS word, '자신감' AS meaning, 'Build your confidence.' AS example, 2 AS ord UNION ALL
    SELECT 'Curious' AS word, '궁금한' AS meaning, 'I am curious about it.' AS example, 3 AS ord UNION ALL
    SELECT 'Generous' AS word, '관대한' AS meaning, 'A generous donor.' AS example, 4 AS ord UNION ALL
    SELECT 'Honest' AS word, '정직한' AS meaning, 'Give an honest opinion.' AS example, 5 AS ord UNION ALL
    SELECT 'Jealous' AS word, '질투하는' AS meaning, 'Don''t be jealous.' AS example, 6 AS ord UNION ALL
    SELECT 'Patience' AS word, '인내심' AS meaning, 'Patience is a virtue.' AS example, 7 AS ord UNION ALL
    SELECT 'Sincere' AS word, '진심 어린' AS meaning, 'A sincere apology.' AS example, 8 AS ord UNION ALL
    SELECT 'Timid' AS word, '소심한' AS meaning, 'He is a timid boy.' AS example, 9 AS ord UNION ALL
    SELECT 'Valuable' AS word, '가치 있는' AS meaning, 'Valuable experience.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 2;

-- medium stage_3
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 3;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Career' AS word, '경력, 직업' AS meaning, 'Choosing a career.' AS example, 1 AS ord UNION ALL
    SELECT 'Colleague' AS word, '동료' AS meaning, 'Work with colleagues.' AS example, 2 AS ord UNION ALL
    SELECT 'Deadline' AS word, '마감일' AS meaning, 'Meet the deadline.' AS example, 3 AS ord UNION ALL
    SELECT 'Employ' AS word, '고용하다' AS meaning, 'Employ new staff.' AS example, 4 AS ord UNION ALL
    SELECT 'Interview' AS word, '면접' AS meaning, 'Job interview.' AS example, 5 AS ord UNION ALL
    SELECT 'Manage' AS word, '관리하다' AS meaning, 'Manage a project.' AS example, 6 AS ord UNION ALL
    SELECT 'Promote' AS word, '승진시키다' AS meaning, 'She was promoted.' AS example, 7 AS ord UNION ALL
    SELECT 'Salary' AS word, '급여' AS meaning, 'Monthly salary.' AS example, 8 AS ord UNION ALL
    SELECT 'Skill' AS word, '기술' AS meaning, 'Technical skills.' AS example, 9 AS ord UNION ALL
    SELECT 'Workplace' AS word, '직장' AS meaning, 'Modern workplace.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 3;

-- medium stage_4
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 4;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Access' AS word, '접근' AS meaning, 'Access to the net.' AS example, 1 AS ord UNION ALL
    SELECT 'Connect' AS word, '연결하다' AS meaning, 'Connect to Wi-Fi.' AS example, 2 AS ord UNION ALL
    SELECT 'Device' AS word, '장치' AS meaning, 'Mobile devices.' AS example, 3 AS ord UNION ALL
    SELECT 'Download' AS word, '다운로드' AS meaning, 'Download the file.' AS example, 4 AS ord UNION ALL
    SELECT 'Feature' AS word, '특징' AS meaning, 'New software features.' AS example, 5 AS ord UNION ALL
    SELECT 'Install' AS word, '설치하다' AS meaning, 'Install an app.' AS example, 6 AS ord UNION ALL
    SELECT 'Network' AS word, '망, 네트워크' AS meaning, 'Social network.' AS example, 7 AS ord UNION ALL
    SELECT 'Privacy' AS word, '개인정보 보호' AS meaning, 'Privacy settings.' AS example, 8 AS ord UNION ALL
    SELECT 'Update' AS word, '업데이트' AS meaning, 'System update.' AS example, 9 AS ord UNION ALL
    SELECT 'Virtual' AS word, '가상의' AS meaning, 'Virtual reality.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 4;

-- medium stage_5
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 5;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Citizen' AS word, '시민' AS meaning, 'Rights of citizens.' AS example, 1 AS ord UNION ALL
    SELECT 'Community' AS word, '공동체' AS meaning, 'Local community.' AS example, 2 AS ord UNION ALL
    SELECT 'Crime' AS word, '범죄' AS meaning, 'Fight against crime.' AS example, 3 AS ord UNION ALL
    SELECT 'Evidence' AS word, '증거' AS meaning, 'Lack of evidence.' AS example, 4 AS ord UNION ALL
    SELECT 'Government' AS word, '정부' AS meaning, 'Central government.' AS example, 5 AS ord UNION ALL
    SELECT 'Justice' AS word, '정의' AS meaning, 'Seek justice.' AS example, 6 AS ord UNION ALL
    SELECT 'Lawyer' AS word, '변호사' AS meaning, 'Consult a lawyer.' AS example, 7 AS ord UNION ALL
    SELECT 'Policy' AS word, '정책' AS meaning, 'Company policy.' AS example, 8 AS ord UNION ALL
    SELECT 'Society' AS word, '사회' AS meaning, 'Modern society.' AS example, 9 AS ord UNION ALL
    SELECT 'Vote' AS word, '투표하다' AS meaning, 'Right to vote.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 5;

-- medium stage_6
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 6;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Climate' AS word, '기후' AS meaning, 'Climate change.' AS example, 1 AS ord UNION ALL
    SELECT 'Disaster' AS word, '재난' AS meaning, 'Natural disaster.' AS example, 2 AS ord UNION ALL
    SELECT 'Ecology' AS word, '생태계' AS meaning, 'Study ecology.' AS example, 3 AS ord UNION ALL
    SELECT 'Energy' AS word, '에너지' AS meaning, 'Solar energy.' AS example, 4 AS ord UNION ALL
    SELECT 'Pollute' AS word, '오염시키다' AS meaning, 'Pollute the river.' AS example, 5 AS ord UNION ALL
    SELECT 'Recycle' AS word, '재활용하다' AS meaning, 'Recycle plastic.' AS example, 6 AS ord UNION ALL
    SELECT 'Resource' AS word, '자원' AS meaning, 'Natural resources.' AS example, 7 AS ord UNION ALL
    SELECT 'Species' AS word, '종' AS meaning, 'Endangered species.' AS example, 8 AS ord UNION ALL
    SELECT 'Waste' AS word, '쓰레기' AS meaning, 'Reduce waste.' AS example, 9 AS ord UNION ALL
    SELECT 'Wildlife' AS word, '야생동물' AS meaning, 'Protect wildlife.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 6;

-- medium stage_7
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 7;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Arrival' AS word, '도착' AS meaning, 'Check arrival time.' AS example, 1 AS ord UNION ALL
    SELECT 'Baggage' AS word, '수하물' AS meaning, 'Carry baggage.' AS example, 2 AS ord UNION ALL
    SELECT 'Confirm' AS word, '확인하다' AS meaning, 'Confirm booking.' AS example, 3 AS ord UNION ALL
    SELECT 'Departure' AS word, '출발' AS meaning, 'Departure gate.' AS example, 4 AS ord UNION ALL
    SELECT 'Destination' AS word, '목적지' AS meaning, 'Final destination.' AS example, 5 AS ord UNION ALL
    SELECT 'Explore' AS word, '탐험하다' AS meaning, 'Explore the city.' AS example, 6 AS ord UNION ALL
    SELECT 'Passenger' AS word, '승객' AS meaning, 'Boarding passengers.' AS example, 7 AS ord UNION ALL
    SELECT 'Reserve' AS word, '예약하다' AS meaning, 'Reserve a seat.' AS example, 8 AS ord UNION ALL
    SELECT 'Ticket' AS word, '티켓' AS meaning, 'Buying a ticket.' AS example, 9 AS ord UNION ALL
    SELECT 'Transport' AS word, '교통수단' AS meaning, 'Public transport.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 7;

-- medium stage_8
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 8;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Budget' AS word, '예산' AS meaning, 'Annual budget.' AS example, 1 AS ord UNION ALL
    SELECT 'Company' AS word, '회사' AS meaning, 'Local company.' AS example, 2 AS ord UNION ALL
    SELECT 'Consumer' AS word, '소비자' AS meaning, 'Consumer behavior.' AS example, 3 AS ord UNION ALL
    SELECT 'Finance' AS word, '재무' AS meaning, 'Finance manager.' AS example, 4 AS ord UNION ALL
    SELECT 'Investment' AS word, '투자' AS meaning, 'Foreign investment.' AS example, 5 AS ord UNION ALL
    SELECT 'Market' AS word, '시장' AS meaning, 'Stock market.' AS example, 6 AS ord UNION ALL
    SELECT 'Profit' AS word, '이익' AS meaning, 'Net profit.' AS example, 7 AS ord UNION ALL
    SELECT 'Project' AS word, '프로젝트' AS meaning, 'Manage a project.' AS example, 8 AS ord UNION ALL
    SELECT 'Purchase' AS word, '구매하다' AS meaning, 'Purchase goods.' AS example, 9 AS ord UNION ALL
    SELECT 'Tax' AS word, '세금' AS meaning, 'Income tax.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 8;

-- medium stage_9
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 9;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Disease' AS word, '질병' AS meaning, 'Prevent disease.' AS example, 1 AS ord UNION ALL
    SELECT 'Exercise' AS word, '운동' AS meaning, 'Daily exercise.' AS example, 2 AS ord UNION ALL
    SELECT 'Health' AS word, '건강' AS meaning, 'Public health.' AS example, 3 AS ord UNION ALL
    SELECT 'Medicine' AS word, '약, 의학' AS meaning, 'Modern medicine.' AS example, 4 AS ord UNION ALL
    SELECT 'Muscle' AS word, '근육' AS meaning, 'Build muscle.' AS example, 5 AS ord UNION ALL
    SELECT 'Patient' AS word, '환자' AS meaning, 'Care for patients.' AS example, 6 AS ord UNION ALL
    SELECT 'Physical' AS word, '신체의' AS meaning, 'Physical activity.' AS example, 7 AS ord UNION ALL
    SELECT 'Recover' AS word, '회복하다' AS meaning, 'Recover from cold.' AS example, 8 AS ord UNION ALL
    SELECT 'Surgery' AS word, '수술' AS meaning, 'Undergo surgery.' AS example, 9 AS ord UNION ALL
    SELECT 'Vitamin' AS word, '비타민' AS meaning, 'Take vitamins.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 9;

-- medium stage_10
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'medium' AND s.stage_number = 10;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Analysis' AS word, '분석' AS meaning, 'Data analysis.' AS example, 1 AS ord UNION ALL
    SELECT 'Concept' AS word, '개념' AS meaning, 'Basic concept.' AS example, 2 AS ord UNION ALL
    SELECT 'Experiment' AS word, '실험' AS meaning, 'Conduct experiment.' AS example, 3 AS ord UNION ALL
    SELECT 'Formula' AS word, '공식' AS meaning, 'Math formula.' AS example, 4 AS ord UNION ALL
    SELECT 'Hypothesis' AS word, '가설' AS meaning, 'Test a hypothesis.' AS example, 5 AS ord UNION ALL
    SELECT 'Knowledge' AS word, '지식' AS meaning, 'Gain knowledge.' AS example, 6 AS ord UNION ALL
    SELECT 'Object' AS word, '물체, 대상' AS meaning, 'Study the object.' AS example, 7 AS ord UNION ALL
    SELECT 'Research' AS word, '연구' AS meaning, 'Scientific research.' AS example, 8 AS ord UNION ALL
    SELECT 'Theory' AS word, '이론' AS meaning, 'Economic theory.' AS example, 9 AS ord UNION ALL
    SELECT 'University' AS word, '대학교' AS meaning, 'Attend university.' AS example, 10 AS ord
) t WHERE s.difficulty = 'medium' AND s.stage_number = 10;

-- Hard Stage_1
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 1;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Ambiguous' AS word, '모호한' AS meaning, 'Ambiguous answer.' AS example, 1 AS ord UNION ALL
    SELECT 'Benevolent' AS word, '자비로운' AS meaning, 'Benevolent king.' AS example, 2 AS ord UNION ALL
    SELECT 'Conspicuous' AS word, '눈에 띄는' AS meaning, 'Conspicuous error.' AS example, 3 AS ord UNION ALL
    SELECT 'Deteriorate' AS word, '악화되다' AS meaning, 'Health deteriorated.' AS example, 4 AS ord UNION ALL
    SELECT 'Eloquent' AS word, '유창한' AS meaning, 'Eloquent speech.' AS example, 5 AS ord UNION ALL
    SELECT 'Frivolous' AS word, '경솔한' AS meaning, 'Frivolous talk.' AS example, 6 AS ord UNION ALL
    SELECT 'Gregarious' AS word, '사교적인' AS meaning, 'Gregarious person.' AS example, 7 AS ord UNION ALL
    SELECT 'Hypocrisy' AS word, '위선' AS meaning, 'End hypocrisy.' AS example, 8 AS ord UNION ALL
    SELECT 'Inevitable' AS word, '피할 수 없는' AS meaning, 'Inevitable result.' AS example, 9 AS ord UNION ALL
    SELECT 'Juxtapose' AS word, '병치하다' AS meaning, 'Juxtapose colors.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 1;

-- Hard Stage_2
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 2;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Astute' AS word, '영리한, 기민한' AS meaning, 'An astute businessman.' AS example, 1 AS ord UNION ALL
    SELECT 'Capricious' AS word, '변덕스러운' AS meaning, 'A capricious climate.' AS example, 2 AS ord UNION ALL
    SELECT 'Diligent' AS word, '부지런한' AS meaning, 'A diligent worker.' AS example, 3 AS ord UNION ALL
    SELECT 'Erudite' AS word, '박식한' AS meaning, 'An erudite scholar.' AS example, 4 AS ord UNION ALL
    SELECT 'Fastidious' AS word, '까다로운' AS meaning, 'Fastidious about cleanliness.' AS example, 5 AS ord UNION ALL
    SELECT 'Gullible' AS word, '잘 속는' AS meaning, 'Too gullible to believe.' AS example, 6 AS ord UNION ALL
    SELECT 'Indifferent' AS word, '무관심한' AS meaning, 'Indifferent to pain.' AS example, 7 AS ord UNION ALL
    SELECT 'Loquacious' AS word, '말이 많은' AS meaning, 'A loquacious speaker.' AS example, 8 AS ord UNION ALL
    SELECT 'Pragmatic' AS word, '실용적인' AS meaning, 'A pragmatic approach.' AS example, 9 AS ord UNION ALL
    SELECT 'Resilient' AS word, '회복력 있는' AS meaning, 'A resilient economy.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 2;

-- Hard Stage_3
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 3;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Advocate' AS word, '옹호하다' AS meaning, 'Advocate for peace.' AS example, 1 AS ord UNION ALL
    SELECT 'Contradict' AS word, '모순되다' AS meaning, 'Contradictory evidence.' AS example, 2 AS ord UNION ALL
    SELECT 'Dispute' AS word, '분쟁' AS meaning, 'A border dispute.' AS example, 3 AS ord UNION ALL
    SELECT 'Exacerbate' AS word, '악화시키다' AS meaning, 'Exacerbate the problem.' AS example, 4 AS ord UNION ALL
    SELECT 'Hostility' AS word, '적대감' AS meaning, 'Open hostility.' AS example, 5 AS ord UNION ALL
    SELECT 'Mitigate' AS word, '완화시키다' AS meaning, 'Mitigate the risk.' AS example, 6 AS ord UNION ALL
    SELECT 'Obscure' AS word, '모호하게 하다' AS meaning, 'Obscure the truth.' AS example, 7 AS ord UNION ALL
    SELECT 'Pervasive' AS word, '만연한' AS meaning, 'Pervasive corruption.' AS example, 8 AS ord UNION ALL
    SELECT 'Refute' AS word, '반박하다' AS meaning, 'Refute a theory.' AS example, 9 AS ord UNION ALL
    SELECT 'Vindicate' AS word, '정당성을 입증하다' AS meaning, 'Vindicated by the result.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 3;

-- Hard Stage_4
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 4;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Ameliorate' AS word, '개선하다' AS meaning, 'Ameliorate conditions.' AS example, 1 AS ord UNION ALL
    SELECT 'Ephemeral' AS word, '수명이 짧은' AS meaning, 'Ephemeral fame.' AS example, 2 AS ord UNION ALL
    SELECT 'Fluctuate' AS word, '변동하다' AS meaning, 'Prices fluctuate.' AS example, 3 AS ord UNION ALL
    SELECT 'Immutable' AS word, '불변의' AS meaning, 'Immutable laws.' AS example, 4 AS ord UNION ALL
    SELECT 'Metamorphosis' AS word, '변형, 탈바꿈' AS meaning, 'A complete metamorphosis.' AS example, 5 AS ord UNION ALL
    SELECT 'Obsolete' AS word, '구식의' AS meaning, 'Obsolete technology.' AS example, 6 AS ord UNION ALL
    SELECT 'Precipitate' AS word, '촉발시키다' AS meaning, 'Precipitate a crisis.' AS example, 7 AS ord UNION ALL
    SELECT 'Stagnant' AS word, '침체된' AS meaning, 'Stagnant economy.' AS example, 8 AS ord UNION ALL
    SELECT 'Transient' AS word, '일시적인' AS meaning, 'Transient fashion.' AS example, 9 AS ord UNION ALL
    SELECT 'Vicissitude' AS word, '우여곡절' AS meaning, 'Life''s vicissitudes.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 4;

-- Hard Stage_5
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 5;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Altruism' AS word, '이타주의' AS meaning, 'Pure altruism.' AS example, 1 AS ord UNION ALL
    SELECT 'Hierarchy' AS word, '계층' AS meaning, 'Social hierarchy.' AS example, 2 AS ord UNION ALL
    SELECT 'Inherent' AS word, '내재된' AS meaning, 'Inherent rights.' AS example, 3 AS ord UNION ALL
    SELECT 'Paradigm' AS word, '패러다임' AS meaning, 'A paradigm shift.' AS example, 4 AS ord UNION ALL
    SELECT 'Pragmatism' AS word, '실용주의' AS meaning, 'Political pragmatism.' AS example, 5 AS ord UNION ALL
    SELECT 'Reverence' AS word, '숭배, 경의' AS meaning, 'Deep reverence.' AS example, 6 AS ord UNION ALL
    SELECT 'Skepticism' AS word, '회의론' AS meaning, 'Healthy skepticism.' AS example, 7 AS ord UNION ALL
    SELECT 'Subjective' AS word, '주관적인' AS meaning, 'Subjective view.' AS example, 8 AS ord UNION ALL
    SELECT 'Utilitarian' AS word, '공리주의의' AS meaning, 'Utilitarian principles.' AS example, 9 AS ord UNION ALL
    SELECT 'Zealous' AS word, '열성적인' AS meaning, 'A zealous supporter.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 5;

-- Hard Stage_6
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 6;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Axiom' AS word, '자명한 이치' AS meaning, 'Mathematical axiom.' AS example, 1 AS ord UNION ALL
    SELECT 'Empirical' AS word, '경험적인' AS meaning, 'Empirical evidence.' AS example, 2 AS ord UNION ALL
    SELECT 'Inference' AS word, '추론' AS meaning, 'Draw an inference.' AS example, 3 AS ord UNION ALL
    SELECT 'Paradox' AS word, '역설' AS meaning, 'A strange paradox.' AS example, 4 AS ord UNION ALL
    SELECT 'Synthesis' AS word, '통합, 합성' AS meaning, 'Synthesis of ideas.' AS example, 5 AS ord UNION ALL
    SELECT 'Versatile' AS word, '다재다능한' AS meaning, 'Versatile tool.' AS example, 6 AS ord UNION ALL
    SELECT 'Abstract' AS word, '추상적인' AS meaning, 'Abstract concept.' AS example, 7 AS ord UNION ALL
    SELECT 'Cognitive' AS word, '인지의' AS meaning, 'Cognitive development.' AS example, 8 AS ord UNION ALL
    SELECT 'Inductive' AS word, '귀납적인' AS meaning, 'Inductive reasoning.' AS example, 9 AS ord UNION ALL
    SELECT 'Nuance' AS word, '뉘앙스, 미묘한 차이' AS meaning, 'Subtle nuances.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 6;

-- Hard Stage_7
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 7;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Autonomy' AS word, '자율성' AS meaning, 'Grant autonomy.' AS example, 1 AS ord UNION ALL
    SELECT 'Bureaucracy' AS word, '관료주의' AS meaning, 'Red tape of bureaucracy.' AS example, 2 AS ord UNION ALL
    SELECT 'Clandestine' AS word, '비밀의' AS meaning, 'Clandestine meeting.' AS example, 3 AS ord UNION ALL
    SELECT 'Democracy' AS word, '민주주의' AS meaning, 'Principles of democracy.' AS example, 4 AS ord UNION ALL
    SELECT 'Hegemony' AS word, '패권' AS meaning, 'Cultural hegemony.' AS example, 5 AS ord UNION ALL
    SELECT 'Lobbyist' AS word, '로비스트' AS meaning, 'Political lobbyists.' AS example, 6 AS ord UNION ALL
    SELECT 'Mandate' AS word, '권한, 명령' AS meaning, 'A popular mandate.' AS example, 7 AS ord UNION ALL
    SELECT 'Sovereignty' AS word, '주권' AS meaning, 'National sovereignty.' AS example, 8 AS ord UNION ALL
    SELECT 'Tyranny' AS word, '폭정' AS meaning, 'Against tyranny.' AS example, 9 AS ord UNION ALL
    SELECT 'Unanimous' AS word, '만장일치의' AS meaning, 'Unanimous decision.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 7;

-- Hard Stage_8
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 8;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Austerity' AS word, '긴축' AS meaning, 'Austerity measures.' AS example, 1 AS ord UNION ALL
    SELECT 'Capitalism' AS word, '자본주의' AS meaning, 'Laissez-faire capitalism.' AS example, 2 AS ord UNION ALL
    SELECT 'Deficit' AS word, '적자' AS meaning, 'Trade deficit.' AS example, 3 AS ord UNION ALL
    SELECT 'Equilibrium' AS word, '균형' AS meaning, 'Market equilibrium.' AS example, 4 AS ord UNION ALL
    SELECT 'Inflation' AS word, '인플레이션' AS meaning, 'High inflation.' AS example, 5 AS ord UNION ALL
    SELECT 'Monopoly' AS word, '독점' AS meaning, 'Natural monopoly.' AS example, 6 AS ord UNION ALL
    SELECT 'Quota' AS word, '할당량' AS meaning, 'Import quota.' AS example, 7 AS ord UNION ALL
    SELECT 'Recession' AS word, '경기 후퇴' AS meaning, 'Economic recession.' AS example, 8 AS ord UNION ALL
    SELECT 'Subsidy' AS word, '보조금' AS meaning, 'Government subsidy.' AS example, 9 AS ord UNION ALL
    SELECT 'Tariff' AS word, '관세' AS meaning, 'Protective tariffs.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 8;

-- Hard Stage_9
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 9;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Affable' AS word, '상냥한' AS meaning, 'An affable host.' AS example, 1 AS ord UNION ALL
    SELECT 'Belligerent' AS word, '호전적인' AS meaning, 'Belligerent nations.' AS example, 2 AS ord UNION ALL
    SELECT 'Complacent' AS word, '자기만족의' AS meaning, 'Don''t be complacent.' AS example, 3 AS ord UNION ALL
    SELECT 'Deference' AS word, '존중' AS meaning, 'Out of deference.' AS example, 4 AS ord UNION ALL
    SELECT 'Empathy' AS word, '공감' AS meaning, 'Deep empathy.' AS example, 5 AS ord UNION ALL
    SELECT 'Fidelity' AS word, '충성' AS meaning, 'Marital fidelity.' AS example, 6 AS ord UNION ALL
    SELECT 'Insolent' AS word, '무례한' AS meaning, 'An insolent child.' AS example, 7 AS ord UNION ALL
    SELECT 'Magnanimous' AS word, '도량이 넓은' AS meaning, 'A magnanimous winner.' AS example, 8 AS ord UNION ALL
    SELECT 'Narcissism' AS word, '자기애' AS meaning, 'Extreme narcissism.' AS example, 9 AS ord UNION ALL
    SELECT 'Venerable' AS word, '경건한, 존경받는' AS meaning, 'Venerable leader.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 9;

-- Hard Stage_10
DELETE w FROM words w
JOIN stages s ON s.id = w.stage_id
WHERE s.difficulty = 'hard' AND s.stage_number = 10;
INSERT INTO words (created_at, stage_id, word, meaning, example, image_url, order_index)
SELECT NOW(6), s.id, t.word, t.meaning, t.example, NULL, t.ord
FROM stages s CROSS JOIN (
    SELECT 'Anomaly' AS word, '이례적인 것' AS meaning, 'A genetic anomaly.' AS example, 1 AS ord UNION ALL
    SELECT 'Coherent' AS word, '일관성 있는' AS meaning, 'A coherent argument.' AS example, 2 AS ord UNION ALL
    SELECT 'Dilapidated' AS word, '황폐한' AS meaning, 'A dilapidated building.' AS example, 3 AS ord UNION ALL
    SELECT 'Exuberant' AS word, '활기 넘치는' AS meaning, 'Exuberant energy.' AS example, 4 AS ord UNION ALL
    SELECT 'Fortuitous' AS word, '우연한, 행운의' AS meaning, 'Fortuitous encounter.' AS example, 5 AS ord UNION ALL
    SELECT 'Incessant' AS word, '끊임없는' AS meaning, 'Incessant rain.' AS example, 6 AS ord UNION ALL
    SELECT 'Lucid' AS word, '명쾌한' AS meaning, 'A lucid explanation.' AS example, 7 AS ord UNION ALL
    SELECT 'Mundane' AS word, '일상적인, 세속적인' AS meaning, 'Mundane tasks.' AS example, 8 AS ord UNION ALL
    SELECT 'Opulent' AS word, '호화로운' AS meaning, 'Opulent lifestyle.' AS example, 9 AS ord UNION ALL
    SELECT 'Pristine' AS word, '오염되지 않은' AS meaning, 'Pristine forest.' AS example, 10 AS ord
) t WHERE s.difficulty = 'hard' AND s.stage_number = 10;

