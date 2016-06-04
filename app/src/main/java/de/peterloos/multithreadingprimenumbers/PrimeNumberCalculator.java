package de.peterloos.multithreadingprimenumbers;

/**
 * Created by Peter on 04.06.2016.
 */
public class PrimeNumberCalculator implements Runnable {

    private long minimum;
    private long maximum;
    private long next;

    private UpdateUIHandlerListener listener;

    public PrimeNumberCalculator()
    {
        this.minimum = 2;
        this.maximum = 1000;
        this.next = this.minimum;

        this.listener = null;
    }

    // properties
    public long getMinimum () {
        return this.minimum;
    }

    public void setMinimum (long value) {
        this.minimum = value;
        this.next = this.minimum;
    }

    public long getMaximum () {
        return this.maximum;
    }

    public void setMaximum (long value) {
        this.maximum = value;
    }

    // public interface
    public void run ()
    {
        long max;
        long next;

        int local = 0;

        synchronized (this)
        {
            // retrieve upper prime number limit
            max = this.maximum;

            // retrieve next prime number candidate
            next = this.next;

            // increment current prime number candidate
            // for next available client
            this.next++;
        }

        while (next < max)
        {
            if (PrimeNumberCalculator.IsPrime(next))
            {
                // increment thread specific prime number counter
                local++;
            }

            synchronized (this)
            {
                // retrieve next prime number candidate
                next = this.next;

                // adjust current prime number candidate
                // for next available client
                this.next++;

                // skip even numbers
                if (this.next % 2 == 0)
                    this.next++;
            }
        }

        // calculation done, fire event with results
        if (this.listener != null)
        {
            long tid = Thread.currentThread().getId();
            this.listener.UpdateUIHandler(tid, local);
        }
    }

    public void setUpdateUIHandlerListener(UpdateUIHandlerListener listener) {
        this.listener = listener;
    }

    // private helper methods
    private static boolean IsPrime(long number)
    {
        // the smallest prime number is 2
        if (number <= 2)
            return number == 2;

        // even numbers other than 2 are not prime
        if (number % 2 == 0)
            return false;

        // check odd divisors from 3 to the square root of the number
        long end = (long) Math.sqrt(number);
        for (long i = 3; i <= end; i += 2)
            if (number % i == 0)
                return false;

        // found prime number
        return true;
    }

}
