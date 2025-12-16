-- Vector Index
ALTER TABLE video_link ADD VECTOR INDEX (embedding) M=8 DISTANCE=cosine;
ALTER TABLE study_word ADD VECTOR INDEX (embedding) M=8 DISTANCE=cosine;

