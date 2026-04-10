package backEnd;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;

public class Wallet {
  /**
   * The RandomAccessFile of the wallet file
   */
  private RandomAccessFile file;
  private FileChannel channel; // https://stackoverflow.com/questions/60635390/how-can-i-lock-a-file-from-within-a-java-application-for-all-other-processes-on

  /**
   * Creates a Wallet object
   *
   * A Wallet object interfaces with the wallet RandomAccessFile
   */
  public Wallet() throws Exception {
    this.file = new RandomAccessFile(new File("backEnd/wallet.txt"), "rw");
    this.channel = this.file.getChannel();
  }

  /**
   * Gets the wallet balance.
   *
   * @return The content of the wallet file as an integer
   */
  public int getBalance() throws IOException {
    this.file.seek(0);
    return Integer.parseInt(this.file.readLine());
  }

  /**
   * Sets a new balance in the wallet
   *
   * @param newBalance new balance to write in the wallet
   */
  private void setBalance(int newBalance) throws Exception {
    this.file.setLength(0);
    String str = Integer.valueOf(newBalance).toString() + '\n';
    this.file.writeBytes(str);
  }

  /**
   * Closes the RandomAccessFile in this.file
   */
  public void close() throws Exception {
    this.file.close();
  }

  public boolean safeWithdraw(int valueToWithdraw) throws Exception {
    // Make sure the wallet is locked/atomic so we can check
    FileLock lock = this.channel.lock();

    // Check balance
    int balance = getBalance();

    if (balance < valueToWithdraw) {
      lock.release();
      return false;
    }

    Thread.sleep(10000);

    // Set balance
    setBalance(balance - valueToWithdraw);
    lock.release();
    return true;
  }
}
