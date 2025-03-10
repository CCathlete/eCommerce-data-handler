package org.webcat.ecommerce.datahandler.shared.helpers;

public class SnowflakeIDGenerator
{

  private final Long epoch =
      1741611497L; // Monday, 10 March 2025 13:58:17 GMT+01:00

  private final Long machineId;
  private final Long datacenterId;
  private final Long sequenceBits = 12L;
  private final Long machineIdBits = 5L;
  private final Long datacenterIdBits =
      5L;

  // The max binary number for the id is the left shift (next higher power of 2) minus 1, so that the
  // higher power we just added turns to zero and the bits to the right of it are 1.
  private final Long maxMachineId =
      (1L << machineIdBits) - 1L;

  private final Long maxDatacenterId =
      (1L << datacenterIdBits) - 1L;

  private final Long sequenceMask =
      (1L << sequenceBits) - 1L;

  private Long previousTimestamp = -1L;
  private Long sequence = 0L;

  public SnowflakeIDGenerator(
      Long datacenterId, Long machineId)
  {
    if (machineId > this.maxMachineId
        || machineId < 0L)
    {
      throw new IllegalArgumentException(
          "Machine ID out of range");
    }

    if (datacenterId > this.maxDatacenterId
        || datacenterId < 0L)
    {
      throw new IllegalArgumentException(
          "Datacenter ID out of range");
    }
    this.datacenterId = datacenterId;
    this.machineId = machineId;
  }

  // The synchromized keyword locks the method so that only one
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
      // Why are we doing this?
      this.sequence =
          (this.sequence + 1)
              & sequenceMask;
      if (this.sequence == 0L)
      {
        timestamp =
            System.currentTimeMillis();
        while (timestamp <= this.previousTimestamp)
        {
          // In case there was a clock change and the time was reset
          // backwards, we're waiting until we reach
          // the previous timestamp.
        }
      }
    } else
    {
      this.sequence = 0L;
    }

    this.previousTimestamp = timestamp;

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
