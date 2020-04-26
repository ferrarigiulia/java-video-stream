import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Alexandre Sturmer Wolf
 */
public class EnviadorBlocosAleatorios {
    public static int valueY = 0; 
    public static void main(String[] args) {
        // buffer para armazenar o bloco da tela
        byte buffer[] = new byte[Util.BLOCK_X * Util.BLOCK_Y * 4 + 4 + 4]; // pegar R, G, B, e alfa para cada pixel + 4 pois quero informar o posX e  + 4 posY (posicão sorteada)

        try {

            Robot robot = new Robot();
            DatagramSocket senderSocket = new DatagramSocket();
            InetAddress ipDestino = InetAddress.getByName("127.0.0.1"); // destinatário
           

            while (true) {
                      
                new Thread() {                  
                    @Override
                    public void run() {                     
                        quadrante(robot, buffer, senderSocket, ipDestino, 0, 0, 1366, 768, valueY, 1);              
                    }
                }.start();

                new Thread() {                  
                    @Override
                    public void run() {                     
                        quadrante(robot, buffer, senderSocket, ipDestino, 0, 683, 1366, 768, valueY, 2);              
                    }
                }.start();

                new Thread() {                  
                    @Override
                    public void run() {                     
                        quadrante(robot, buffer, senderSocket, ipDestino, 384, 0, 1366, 768, valueY, 3);              
                    }
                }.start();

                new Thread() {                  
                    @Override
                    public void run() {                     
                        quadrante(robot, buffer, senderSocket, ipDestino, 384, 683, 1366, 768, valueY, 4);              
                    }
                }.start();
            
            
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    
}

    public static void quadrante(Robot robot, byte[] buffer, DatagramSocket senderSocket, InetAddress ipDestino, int yPos, int xPos, int resolX, int resolY, int varY, int quadrante){
        try {

            BufferedImage bi = robot.createScreenCapture(new Rectangle(resolX, resolY)); // capturei a tela toda
            int varX = 0;
            varX = xPos;

            int posX = 0;
            int posY = 0;
        
            for (int i = 0; i < 5; i++) { // percorre metade dos 10 quadros necessários pra preencher a tela (1366 / 136)      
               
        
                posX = varX; // calcula posicao de x
                posY = yPos + varY; // calcula posicao de Y que é incrementado ao final do for                                  
                varX = varX + 136;  // incrementa um bloco para x
              
              
                // arqui poderia ser lancado uma Thread
                int aux = 0;
                for (int y = 0; y < Util.BLOCK_Y; y++) {
                    for (int x = 0; x < Util.BLOCK_X; x++) {

                        int cor = bi.getRGB(posX + x, posY + y); //ARGB

                        byte auxBuffer[] = Util.integerToBytes(cor);
                        for (int j = 0; j < auxBuffer.length; j++) {
                            buffer[aux++] = auxBuffer[j];
                        }
                    }
                }

                // bytes do posX
                byte auxBufferPosX[] = Util.integerToBytes(posX);
                for (int j = 0; j < auxBufferPosX.length; j++) {
                    buffer[aux++] = auxBufferPosX[j];
                }

                // bytes do posY
                byte auxBufferPosY[] = Util.integerToBytes(posY);
                for (int j = 0; j < auxBufferPosY.length; j++) {
                    buffer[aux++] = auxBufferPosY[j];
                }

                DatagramPacket enviaPacote = new DatagramPacket(buffer, buffer.length, ipDestino, Util.PORTA);
                senderSocket.send(enviaPacote);

                Thread.sleep(10);
            }
            if(valueY >= 384){
                valueY = 0;
            } else {
                valueY = valueY + 64;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }


}


