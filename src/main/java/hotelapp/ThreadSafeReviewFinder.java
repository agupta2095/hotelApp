package hotelapp;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeReviewFinder extends  ReviewsFinder{
    private ReentrantReadWriteLock lock;

    public ThreadSafeReviewFinder() {
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public void putInfoInMap(String hotelId, Set<Review> reviewSet) {
        try {
            lock.writeLock().lock();
            super.putInfoInMap(hotelId, reviewSet);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void findReview(String hotelId, AppInterface appInterface) {
        try {
            lock.readLock().lock();
            super.findReview(hotelId, appInterface);
        } finally {
           lock.readLock().unlock();
        }
    }

    @Override
    public void printInOutputFile(PrintWriter pr, String hotelId) {
        try {
            lock.readLock().lock();
            super.printInOutputFile(pr, hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<Review> getReviewsForAHotel(String hotelId) {
        try {
            lock.readLock().lock();
            return super.getReviewsForAHotel(hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public HashSet<Review> getAllReviews() {
        try {
            lock.readLock().lock();
            return super.getAllReviews();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void fillTotalRatings() {
        try {
            lock.readLock().lock();
            super.fillTotalRatings();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Map<String, Double> getAvgRating(String hotelId) {
        try {
            lock.readLock().lock();
            return super.getAvgRating(hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }
}
