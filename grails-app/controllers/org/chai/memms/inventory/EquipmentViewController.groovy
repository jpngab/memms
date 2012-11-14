/** 
 * Copyright (c) 2012, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.memms.inventory

import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.memms.AbstractController;
import org.chai.memms.inventory.Equipment.Donor;
import org.chai.memms.inventory.Equipment.PurchasedBy;
import org.chai.memms.inventory.EquipmentStatus.Status;
import org.chai.memms.inventory.Equipment;
import org.chai.memms.inventory.EquipmentType;
import org.chai.memms.inventory.Provider;
import org.chai.location.DataLocation;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.memms.security.User;
import org.chai.memms.security.User.UserType;
import org.chai.task.EquipmentExportFilter;

/**
 * @author Jean Kahigiso M.
 *
 */
class EquipmentViewController extends AbstractController {
	
	def providerService
	def equipmentService
	def inventoryService
	def grailsApplication
	def equipmentStatusService

	def getLabel() {
		return "equipment.label";
	}

	def getEntityClass() {
		return Equipment.class;
	}
	
	def list={
		DataLocation dataLocation = DataLocation.get(params.int('dataLocation.id'))
		
		if (dataLocation == null)
			response.sendError(404)

		adaptParamsForList()
		def equipments = equipmentService.getEquipmentsByDataLocation(dataLocation,params)
		render(view:"/entity/list", model:[
					template:"equipment/equipmentList",
					filterTemplate:"equipment/equipmentFilter",
					listTop:"equipment/listTop",
					dataLocation:dataLocation,
					entities: equipments,
					entityCount: equipments.totalCount,
					code: getLabel()
				])
	}

	def search = {
		DataLocation dataLocation = DataLocation.get(params.int("dataLocation.id"));
		
		if (dataLocation == null)
			response.sendError(404)

		adaptParamsForList()
		List<Equipment> equipments = equipmentService.searchEquipment(params['q'],dataLocation,params)
		render (view: '/entity/list', model:[
					template:"equipment/equipmentList",
					filterTemplate:"equipment/equipmentFilter",
					listTop:"equipment/listTop",
					dataLocation:dataLocation,
					entities: equipments,
					entityCount: equipments.totalCount,
					code: getLabel(),
					q:params['q']
				])
		
	}

	def summaryPage = {
		if(user.location instanceof DataLocation) redirect(controller:"equipmentView",action:"list",params:['dataLocation.id':user.location.id])

		def location = Location.get(params.long('location'))
		def dataLocationTypesFilter = getLocationTypes()
		def template = null
		def inventories = null

		adaptParamsForList()

		def locationSkipLevels = inventoryService.getSkipLocationLevels()

		if (location != null) {
			template = '/inventorySummaryPage/sectionTable'
			inventories = inventoryService.getInventoryByLocation(location,dataLocationTypesFilter,params)
		}

		render (view: '/inventorySummaryPage/summaryPage', model: [
					inventories:inventories?.inventoryList,
					currentLocation: location,
					currentLocationTypes: dataLocationTypesFilter,
					template: template,
					entityCount: inventories?.totalCount,
					locationSkipLevels: locationSkipLevels
				])
	}

	def generalExport = { ExportFilterCommand cmd ->

		// we do this because automatic data binding does not work with polymorphic elements
		Set<CalculationLocation> calculationLocations = new HashSet<CalculationLocation>()
		params.list('calculationLocationids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def calculationLocation = CalculationLocation.get(id)
				if (calculationLocation != null && !calculationLocations.contains(calculationLocation)) calculationLocations.add(calculationLocation);
			}
		}
		cmd.calculationLocations = calculationLocations

		Set<DataLocationType> dataLocationTypes = new HashSet<DataLocationType>()
		params.list('dataLocationTypeids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def dataLocationType = DataLocationType.get(id)
				if (dataLocationType != null && !dataLocationTypes.contains(dataLocationType)) dataLocationTypes.add(dataLocationType);
			}
		}
		cmd.dataLocationTypes = dataLocationTypes

		Set<EquipmentType> equipmentTypes = new HashSet<EquipmentType>()
		params.list('equipmentTypeids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def equipmentType = EquipmentType.get(id)
				if (equipmentType != null && !equipmentTypes.contains(equipmentType)) equipmentTypes.add(equipmentType);
			}
		}
		cmd.equipmentTypes = equipmentTypes

		Set<Provider> manufacturers = new HashSet<Provider>()
		params.list('manufacturerids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def manufacturer = Provider.get(id)
				if (manufacturer != null && !manufacturers.contains(manufacturer)) manufacturers.add(manufacturer);
			}
		}
		cmd.manufacturers = manufacturers

		Set<Provider> suppliers = new HashSet<Provider>()
		params.list('supplierids').each { id ->
			if (NumberUtils.isDigits(id)) {
				def supplier = Provider.get(id)
				if (supplier != null && !suppliers.contains(supplier)) suppliers.add(supplier);
			}
		}
		cmd.suppliers = suppliers
		
		Set<Provider> serviceProviders = new HashSet<Provider>()
		params.list('serviceProvidersIds').each { id ->
			if (NumberUtils.isDigits(id)) {
				def serviceProvider = Provider.get(id)
				if (serviceProvider != null && !serviceProviders.contains(serviceProvider)) serviceProviders.add(serviceProvider);
			}
		}
		cmd.serviceProviders = serviceProviders

		if (log.isDebugEnabled()) log.debug("equipments.export, command="+cmd+", params"+params)


		if(params.exported != null){
			def equipmentExportTask = new EquipmentExportFilter(calculationLocations:cmd.calculationLocations,dataLocationTypes:cmd.dataLocationTypes,
					equipmentTypes:cmd.equipmentTypes,serviceProviders:cmd.serviceProviders,manufacturers:cmd.manufacturers,suppliers:cmd.suppliers,equipmentStatus:cmd.equipmentStatus,
					purchaser:cmd.purchaser,obsolete:cmd.obsolete).save(failOnError: true,flush: true)
			params.exportFilterId = equipmentExportTask.id
			params.class = "EquipmentExportTask"
			params.targetURI = "/equipmentView/generalExport"
			redirect(controller: "task", action: "create", params: params)
		}
		adaptParamsForList()
		render(view:"/entity/equipment/equipmentExportPage", model:[
					template:"/entity/equipment/equipmentExportFilter",
					filterCmd:cmd,
					dataLocationTypes:DataLocationType.list(),
					code: getLabel()
				])
	}

	def filter = { FilterCommand cmd ->
		if (log.isDebugEnabled()) log.debug("equipments.filter, command "+cmd)
		if (cmd.dataLocation == null)
			response.sendError(404)

		adaptParamsForList()
		def equipments = equipmentService.filterEquipment(cmd.dataLocation,cmd.supplier,cmd.manufacturer,cmd.serviceProvider,cmd.equipmentType,cmd.purchaser,cmd.donor,cmd.obsolete,cmd.status,params)
		render (view: '/entity/list', model:[
					template:"equipment/equipmentList",
					filterTemplate:"equipment/equipmentFilter",
					listTop:"equipment/listTop",
					dataLocation:cmd.dataLocation,
					entities: equipments,
					entityCount: equipments.totalCount,
					code: getLabel(),
					filterCmd:cmd,
					q:params['q']
				])

	}

	def export = { FilterCommand cmd ->
		if (log.isDebugEnabled()) log.debug("equipments.export, command "+cmd)
		def dataLocation = DataLocation.get(params.int('dataLocation.id'))
		if (dataLocation == null)
			response.sendError(404)
		adaptParamsForList()

		def equipments = equipmentService.filterEquipment(dataLocation,cmd.supplier,cmd.manufacturer,cmd.serviceProvider,cmd.equipmentType,cmd.purchaser,cmd.donor,cmd.obsolete,cmd.status,params)
		File file = equipmentService.exporter(dataLocation,equipments)

		response.setHeader "Content-disposition", "attachment; filename=${file.name}.csv"
		response.contentType = 'text/csv'
		response.outputStream << file.text
		response.outputStream.flush()
	}

	def updateObsolete = {
		if (log.isDebugEnabled()) log.debug("updateObsolete equipment.obsolete "+params['equipment.id'])
		Equipment equipment = Equipment.get(params.int(['equipment.id']))
		def property = params['field'];
		if (equipment == null || property ==null)
			response.sendError(404)
		else {
			def value= false; def entity = null;
			if(property.equals("obsolete")){
				if(equipment.obsolete) equipment.obsolete = false
				else equipment.obsolete = true
				entity = equipment.save(flush:true)

			}
			if(entity!=null) value=true
			render(contentType:"text/json") { results = [value]}
		}
	}

	def getAjaxData = {

		DataLocation dataLocation = null
		if(params['dataLocation.id']) dataLocation = DataLocation.get(params.int("dataLocation.id"))
		List<Equipment> equipments =[]
		if(dataLocation) equipments = equipmentService.searchEquipment(params['term'],dataLocation, [:])
		else equipments = equipmentService.searchEquipment(params['term'],null, [:])
		render(contentType:"text/json") {
			elements = array {
				equipments.each { equipment ->
					elem (
							key: equipment.id,
							value: equipment.code
							)
				}
			}
			htmls = array {
				equipments.each { equipment ->
					elem (
							key: equipment.id,
							html: g.render(template:"/templates/equipmentFormSide",model:[equipment:equipment,cssClass:"form-aside-hidden",field:"equipment"])
							)
				}
			}
		}

	}

}
class FilterCommand {
	DataLocation dataLocation
	EquipmentType equipmentType
	Provider manufacturer
	Provider supplier
	Provider serviceProvider
	Status status = Status.NONE
	PurchasedBy purchaser
	String obsolete
	Donor donor

	public boolean getObsoleteStatus(){
		if(obsolete) return null
		else if(obsolete.equals("true")) return true
		else if(obsolete.equals("false")) return false
	}

	static constraints = {

		equipmentType nullable:true
		manufacturer nullable:true
		serviceProvider nullable: true
		supplier nullable:true
		status nullable:true
		purchaser nullable:true
		obsolete nullable:true
		donor nullable:true

		dataLocation nullable:false, validator:{val, obj ->
			return (obj.equipmentType != null || obj.manufacturer != null || obj.serviceProvider != null || obj.supplier != null || (obj.status != null && obj.status != Status.NONE) || obj.purchaser || obj.obsolete)?true:"select.atleast.one.value.text"
		}
	}

	String toString() {
		return "FilterCommand[DataLocation="+dataLocation+", EquipmentType="+equipmentType+
		", Manufacturer="+manufacturer+", Supplier="+supplier+", ServiceProvider="+serviceProvider+", Status="+status+", donated="+purchaser+", obsolete="+obsolete+
		", donor=" + donor + "]"
	}
}

class ExportFilterCommand {
	Set<CalculationLocation> calculationLocations
	Set<DataLocationType> dataLocationTypes
	Set<EquipmentType> equipmentTypes
	Set<Provider> manufacturers
	Set<Provider> suppliers
	Set<Provider> serviceProviders
	Status equipmentStatus
	PurchasedBy purchaser
	String obsolete
	Donor donor

	public boolean getObsoleteStatus(){
		if(obsolete) return null
		else if(obsolete.equals("true")) return true
		else if(obsolete.equals("false")) return false
	}

	static constraints = {
		calculationLocations nullable:true
		dataLocationTypes nullable:true
		equipmentTypes nullable:true
		manufacturers nullable:true
		suppliers nullable:true
		serviceProviders nullable:true
		equipmentStatus nullable:true
		purchaser nullable:true
		obsolete nullable:true
		donor nullable:true
	}

	String toString() {
		return "ExportFilterCommand[ CalculationLocations="+calculationLocations+", DataLocationTypes="+dataLocationTypes+" , EquipmentTypes="+equipmentTypes+
		", Manufacturers="+manufacturers+", Suppliers="+suppliers+", ServiceProviders="+serviceProviders+", Status="+equipmentStatus+", donated="+purchaser+", obsolete="+obsolete+
		", donor=" + donor + "]"
	}
}
