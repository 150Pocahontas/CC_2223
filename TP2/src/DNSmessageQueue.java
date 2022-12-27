import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe que implementa uma queue de pacotes
 */
class DNSmessageQueue {
    private LinkedList<DNSmessage> DNSmessages;
    private List
    private ReentrantLock lock;
    private Condition con;

    /**
     * Construtor da classe DNSmessageQueue
     */
    public DNSmessageQueue() {
        DNSmessages = new LinkedList<>();
        lock = new ReentrantLock();
        con = lock.newCondition();
    }

    /**
     * Método que adiciona um Pacote à queue
     *
     * @param DNSmessage    Pacote a ser adicionado
     */
    public void add(DNSmessage DNSmessage) {
        lock.lock();
        try {
            DNSmessages.add(DNSmessage);
            con.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método que adiciona um pacote ao início da queue (Alta prioriodade)
     *
     * @param DNSmessage    Pacote a ser adicionado
     */
    public void addFirst(DNSmessage DNSmessage) {
        lock.lock();
        try {
            DNSmessages.addFirst(DNSmessage);
            con.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método que remove um pacote da queue
     *
     * @return  Pacote removido
     * @throws InterruptedException
     */
    public DNSmessage remove() throws InterruptedException {
        lock.lock();
        try {
            while (DNSmessages.isEmpty())
                con.await();

            return DNSmessages.isEmpty() ? null : DNSmessages.remove();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método que verifica se a queue está vazia
     *
     * @return  Boleano com o resultado
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return DNSmessages.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Método que envia um sinal quando a queue estiver vazia
     */
    public void signalCon() {
        lock.lock();
        try {
            while(!DNSmessages.isEmpty());

            con.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
