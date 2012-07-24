package org.chai.memms.security

import org.chai.memms.AbstractController
import org.apache.shiro.crypto.hash.Sha256Hash;

class UserController  extends  AbstractController{
	def userService
	
	def index(){
		render("You are here")
	}
	def getEntity(def id) {
		return User.get(id)
	}

	def createEntity() {
		return new User()
	}

	def getLabel() {
		return 'user.label'
	}
	
	def getModel(def entity) {
		def dataLocations = []
		if (entity.location != null) dataLocations << entity.location
		[
			user:entity,
			roles: Role.list(),
			dataLocations: dataLocations,
			cmd: params['cmd']
		]
	}
	
	def getEntityClass(){
		return User.class;
	}
	
	def getTemplate() {
		return "/entity/user/createUser"
	}
	
	def validateEntity(def entity) {
		//TODO check for duplicate code
		boolean valid = entity.validate() && params['cmd'].validate()
		if (log.isDebugEnabled()) log.debug ("validation for command object ${params['cmd']}: ${params['cmd'].errors}}")
		return valid;
	}
	
	def saveEntity(def entity) {
		entity.setDefaultPermissions()
		entity.setDefaultRoles()
		entity.save()
	}
	
	def bindParams(def entity) {
		if (log.isDebugEnabled()) log.debug('binding params: '+params)
		bindData(entity,params,[exclude:['uuid','passwordHash']])
		
		if(entity.id==null)
			entity.uuid = UUID.randomUUID().toString();
					
		if(params['cmd']?.password != null && !params['cmd']?.password.equals(''))
			entity.passwordHash = new Sha256Hash(params['cmd'].password).toHex();
	}
	
	def save = { PasswordCommand cmd ->
		if (log.isDebugEnabled()) log.debug("create.userPassword, params:"+params+"command"+cmd)
		params['cmd'] = cmd;
		super.save()
	}
	
	def list = {
		
		List<User> users = User.list();

		render (view: '/entity/list', model:[users:users])
	}

	def search = {
		adaptParamsForList()
		
		List<User> users = userService.searchUser(params['q'], params);

		render (view: '/entity/list', model:[
			template:"user/userList",
			entities: users,
			entityCount: userService.countUser(params['q']),
			code: getLabel(),
			search: true
		])
		
	}
}

class PasswordCommand {
	String password
	String repeat

	static constraints = {
		password(blank: true,nullable:true, minSize: 4)
		repeat(nullable:true, validator: {val, obj ->
			val == obj.password
		})
	}
	
	String toString() {
		return "PasswordCommand[password="+password+", repeat="+repeat+"]"
	}
}
