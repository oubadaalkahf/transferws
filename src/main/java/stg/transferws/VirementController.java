package stg.transferws;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import stg.transferws.dto.OperationRequestDTO;
import stg.transferws.dto.UserDto;
import stg.transferws.entities.Virement;
import stg.transferws.services.impl.IVirementServiceImpl;

@AllArgsConstructor
@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class VirementController {
	@Autowired IVirementServiceImpl virementServiceImpl;
	

    @PutMapping("/do/operation")
    void doOperation(@RequestBody OperationRequestDTO operationDTO) throws Exception {

           if(operationDTO.getOperation_type().equalsIgnoreCase("retrait")) {
        	virementServiceImpl.retirer(operationDTO.getEmetteur(), operationDTO.getMontant());
        }
        else if(operationDTO.getOperation_type().equalsIgnoreCase("versement")) {
        	System.out.println("hiiiiii");
        	virementServiceImpl.verser(operationDTO.getEmetteur(), operationDTO.getMontant());
        }
        else if(operationDTO.getOperation_type().equalsIgnoreCase("virement")) {
        	   UserDto emetteur= virementServiceImpl.consulteUserAccount(operationDTO.getEmetteur());
        	   UserDto destinataire= virementServiceImpl.consulteUserAccount(operationDTO.getDestinataire());
        	virementServiceImpl.virement(operationDTO.getEmetteur(), operationDTO.getDestinataire(), operationDTO.getMontant());
        	Virement virement = new Virement(null,new Date(),emetteur.getRib(),destinataire.getRib(),operationDTO.getMontant());
        	virementServiceImpl.addVirement(virement);
        }
        else {
            throw new Exception("operation invalide");
        }
    }

    @GetMapping("hello")
    String message(@RequestParam String message) {
    	return message;
    }


}
