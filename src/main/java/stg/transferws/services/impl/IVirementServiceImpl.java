package stg.transferws.services.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import stg.transferws.ResponseHandler.ResponseHandler;
import stg.transferws.dto.UserDto;
import stg.transferws.entities.Virement;
import stg.transferws.feign.UserAttijariFeign;
import stg.transferws.repositories.VirementRepository;
import stg.transferws.services.IVirementService;

@Service
@Transactional
public class IVirementServiceImpl implements IVirementService{

	@Autowired UserAttijariFeign attijariFeign;
	@Autowired VirementRepository virementRepository;
	
	@Override
	public UserDto consulteUserAccount(String rib) throws Exception {
		 Optional<UserDto> userdto = attijariFeign.loadUser(rib);
		 System.out.println(attijariFeign.loadUser(rib).get());
		if(userdto.isPresent()) {
			UserDto user = userdto.get(); 
			return user;
		}
		throw new Exception("User Not found");
	}

	@Override
	public ResponseEntity<Object> findAllUsers() {
	List<UserDto> users= attijariFeign.loadUsers();
	if(!users.isEmpty()) {
		return ResponseHandler.generateResponse("users found", HttpStatus.OK, users);
	}
	return ResponseHandler.generateResponse("user not found", HttpStatus.NOT_FOUND, null);
	}

	@Override
	public void retirer(String rib, double montant) throws Exception {
	UserDto userDto = consulteUserAccount(rib);
	System.out.println(userDto.toString());
	userDto.setSolde(userDto.getSolde() - montant);
	if(userDto.getSolde()>=0) {
		System.out.println("retiréééééééééééé");
		attijariFeign.retirerSolde(userDto.getRib(), userDto.getSolde());
		
	}else {
		userDto.setSolde(userDto.getSolde() + montant);
		 throw new Exception("cannot withdraw money ,solde is not enough");
	}
	
	}

	@Override
	public void verser(String rib, double montant) throws Exception {
		UserDto userdto = consulteUserAccount(rib);
		System.out.println("verseéééééééééééééééééééééé");
		System.out.println(userdto.getSolde() + montant);
		 
		userdto.setSolde(userdto.getSolde() + montant);
		attijariFeign.retirerSolde(rib, userdto.getSolde());
	}

	@Override
	public void virement(String emetteur, String destinataire, double montant) throws Exception {
				System.out.println(emetteur);
				System.out.println("hiiiiiiiiiiiiiiii");
		retirer(emetteur, montant);//si exception rollback
	        verser(destinataire, montant);
		
	}

	@Override
	public Virement addVirement(Virement virement) {
		return virementRepository.save(virement);
		
	}
	
	

	

}
