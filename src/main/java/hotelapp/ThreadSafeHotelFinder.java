package hotelapp;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread Safe class to access objects in Hotel Finder
 * Contains override methods from HotelFinder class
 */
public class ThreadSafeHotelFinder extends HotelFinder{
    private ReentrantReadWriteLock lock;

    public ThreadSafeHotelFinder() {
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public void putInfoInMap(String hotelId, HotelInformation hotelInfoObj) {
        try {
            lock.writeLock().lock();
            super.putInfoInMap(hotelId, hotelInfoObj);
        }finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void findHotel(String hotelId) {
        try {
            lock.readLock().lock();
            super.findHotel(hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void printInOutputFile(PrintWriter pr, ReviewsFinder reviewsFinder) {
        try {
            lock.readLock().lock();
            super.printInOutputFile(pr, reviewsFinder);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<HotelInformation> searchHotels(String keyword) {
        try {
            lock.readLock().lock();
            return super.searchHotels(keyword);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    HotelInformation getHotel(String hotelId) {
        try {
            lock.readLock().lock();
            return super.getHotel(hotelId);
        } finally {
            lock.readLock().unlock();
        }
    }
}
