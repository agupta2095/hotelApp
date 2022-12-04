package hotelapp;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeWordFinder extends  WordFinder{
    private ReentrantReadWriteLock lock;
    public ThreadSafeWordFinder() {
        lock = new ReentrantReadWriteLock();
    }

    @Override
    void populateStopWords(String filename) {
        try {
            lock.writeLock().lock();
            super.populateStopWords(filename);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void parseReviewTextAndFillMap(Review reviewObj) {
        try {
            lock.writeLock().lock();
            super.parseReviewTextAndFillMap(reviewObj);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    void findWord(String word, AppInterface appInterface) {
        try {
            lock.readLock().lock();
            super.findWord(word, appInterface);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void sortWordReviews() {
        try {
            lock.writeLock().lock();
            super.sortWordReviews();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
