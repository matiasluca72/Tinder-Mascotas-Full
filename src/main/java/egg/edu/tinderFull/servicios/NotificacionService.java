package egg.edu.tinderFull.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Matias Luca Soto
 */
@Service
public class NotificacionService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Método para enviar un mail
     * @param cuerpo
     * @param titulo
     * @param mail a enviar el correo
     */
    @Async
    public void enviar(String cuerpo, String titulo, String mail) {
        
        //Creamos el Objeto que contendrá los atributos del correo
        SimpleMailMessage mensaje = new SimpleMailMessage();
        
        //Seteamos los atributos del Objeto SimpleMailMessage
        mensaje.setTo(mail);
        mensaje.setFrom("noreply@tinderFull-mascota.com");
        mensaje.setSubject(titulo);
        mensaje.setText(cuerpo);
        
        //Enviamos el correo haciendo uso de un método del atributo JavaMailSender
        mailSender.send(mensaje);
    }
    
}
