package com.projeto.ads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.projeto.ads.model.Role;
import com.projeto.ads.model.Usuario;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import com.projeto.ads.repository.RoleRepository;
import com.projeto.ads.repository.UserRepository;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class UsuarioController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private RoleRepository roleRepository; 
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passWordEncoder;
	
	@GetMapping("/usuario/inserir")
 	public ModelAndView cadastro() {
		ModelAndView mv= new ModelAndView();
		mv.addObject("usuario", new Usuario());
		mv.addObject("roles", roleRepository.findAll());
		mv.setViewName("Login/cadastro");
		return mv;
	}
	@GetMapping("/login")
	public ModelAndView login() {
		ModelAndView mv = new ModelAndView();
		mv.addObject("usuario", new Usuario());
		mv.setViewName("Login/login");
		return mv;
	}
	
	@PostMapping("/usuario/inserir")
	public ModelAndView salvarUsuario(@ModelAttribute Usuario usuario,
	@RequestParam("confirmPassword") String confirmPassword,
	@RequestParam("roleUser") String roleString,
	@RequestParam("dataNasc") String dataNascimentoString)
	{
	ModelAndView mv = new ModelAndView();
	// Verificar se a senha e a confirmação da senha correspondem
	if (!usuario.getPassword().equals(confirmPassword)) {
	mv.addObject("error", "As senhas não cordespondem");
	mv.setViewName("Login/cadastro");
	return mv;
	}
	Date dataFormatada = null;
	try {
	// Define o formato de entrada da data
	SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
	// Converte a string para um objeto Date
	dataFormatada = formatoEntrada.parse(dataNascimentoString);
	} catch (ParseException e) {
		e.printStackTrace();
	}
	usuario.setDataNascimento(dataFormatada);
	Role role = roleRepository.findByNome(roleString);
	usuario.setRole(role);
	usuario.setPassword(passWordEncoder.encode(usuario.getPassword()));
	usuario.setUsername(usuario.getEmail());
	userRepository.save(usuario);
	mv.setViewName("redirect:/login");
	return mv;
	}
	
	@GetMapping("/dashboard")
	public ModelAndView dashBoard(Authentication authentication) {
		ModelAndView mv = new ModelAndView();
		UserDetails userDetalhe= (UserDetails) authentication.getPrincipal();
		String username= userDetalhe.getUsername();
		int indice = username.indexOf('@');
		String nome= indice !=-1 ? username.substring(0, indice): username;
		mv.addObject("nomeUsuario", nome);
		String papel= authentication.getAuthorities().iterator().next().getAuthority();
		if(papel.equals("ROLE_ADMIN"))
		{
			mv.setViewName("Login/index");
		}
		else if(papel.equals("ROLE_USER")) {
			mv.setViewName("Login/indexUsuario");
		}
		
		return mv;
	}//fim dashboard
	
	
}
