package org.webcat.ecommerce.datahandler.shared.helpers;

public class SnowflakeIDGenerator
{

  // A custom starting time (in milliseconds), we will always take a timestamp of (now - epoch).
  // We take the time difference so the time would be smaller than using full Unix timestamps.
  private final Long epoch =
      1741611497L; // Monday, 10 March 2025 13:58:17 GMT+01:00

  // This makes the snowflake id unique over multiple data centers.
  private final Integer datacenterId;
  // This makes the snowflake id unique over multiple machines/servers.
  private final Integer machineId;
  // In case the same machine generates multiple ids in the same millisecond, we use a counter to
  // create a unique id.
  private Long sequence = 0L;

  private final Integer sequenceBits =
      12;
  private final Integer machineIdBits =
      5;
  private final Integer datacenterIdBits =
      5;

  // The max binary number for the id is the left shift (next higher power of 2) minus 1, so that the
  // higher power we just added turns to zero and the bits to the right of it are 1.
  private final Integer maxMachineId =
      (1 << this.machineIdBits) - 1;

  private final Integer maxDatacenterId =
      (1 << this.datacenterIdBits) - 1;

  // A bitmask that limits the sequence number to sequenceBits bits.
  // Example (0b111111111111) for sequenceBits = 12.
  private final Long sequenceMask =
      (1L << this.sequenceBits) - 1L; // left shift - 1 = max number within the range of sequenceBits.

  // The last timestamp used to generate an ID.
  // If the current timestamp is the same as the last timestamp, we do one of 3 things:

  // 1. Increment the sequence number (if still in the same millisecond).

  // 2. Wait until the next millisecond
  // (if after incrementing and filtering with the mask, the
  // sequence number is 0, i.e. the sequence has overflowed,
  // we have to wait for the next millisecond).

  // 3. Reset the sequence number (if a new millisecond has started).
  private Long previousTimestamp = -1L;

  public SnowflakeIDGenerator(
      Integer datacenterId,
      Integer machineId)
  {
    if (machineId > this.maxMachineId
        || machineId < 0)
    {
      throw new IllegalArgumentException(
          "Machine ID out of range");
    }

    if (datacenterId > this.maxDatacenterId
        || datacenterId < 0)
    {
      throw new IllegalArgumentException(
          "Datacenter ID out of range");
    }
    this.datacenterId = datacenterId;
    this.machineId = machineId;
  }

  // The synchronized keyword locks the method so that only one
  // thread can access it at a time if multiple threads use the same
  // generator instance.
  public synchronized Long generateId()
  {
    Long timestamp =
        System.currentTimeMillis();

    if (timestamp < this.previousTimestamp)
    {
      throw new RuntimeException(
          "Clock moved backwards");
    }

    // If this is the same millisecond as the last time, increment the sequence.
    if (timestamp == this.previousTimestamp)
    {
      /**
       * Explanation in the def of {@link previousTimestamp}
       */
      this.sequence =
          // Bitwise AND with the mask cuts overflowed bits.
          (this.sequence + 1)
              & sequenceMask;
      if (this.sequence == 0L)
      {
        Long initialMillisecond =
            System.currentTimeMillis();
        while (System
            .currentTimeMillis() <= initialMillisecond)
        {
          // In case the sequence has overflowed, we need to wait until the next millisecond.
        }
      }
    } else
    {
      // If this is a new millisecond, reset the sequence to 0.
      this.sequence = 0L;
    }

    this.previousTimestamp = timestamp;

    // The snowflake ID is a 64-bit integer made of:
    // 1. Timestamp (12 bits)
    // 2. Datacenter ID (5 bits)
    // 3. Machine ID (5 bits)
    // 4. Sequence (12 bits)
    // The bitwise OR is a binary addition (without carry)
    // and the left shift takes the entire binary number and puts it to the left while there are zeroes
    // in the original place.
    // Than way, it makes room for another number to squeeze in.
    return ((timestamp
        - epoch) << (this.machineIdBits
            + this.datacenterIdBits
            + this.sequenceBits))
        | (this.datacenterId << (this.machineIdBits
            + this.sequenceBits))
        | (this.machineId << this.sequenceBits)
        | this.sequence;

  }
}
