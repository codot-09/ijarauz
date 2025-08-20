package com.example.ijara.service;

import com.example.ijara.dto.ApiResponse;
import com.example.ijara.dto.request.ReqContract;
import com.example.ijara.dto.response.ResContract;
import com.example.ijara.dto.response.ResPageable;
import com.example.ijara.entity.User;
import com.example.ijara.entity.enums.ContractStatus;

import java.util.UUID;

public interface ContractService {
        ApiResponse<String> saveContract(User user, ReqContract reqContract);
        ApiResponse<String> updateContact(UUID contactId, User user, ReqContract reqContract);
        ApiResponse<String> deleteContract(User user, UUID contractId);
        ApiResponse<String> updateStatusContract(UUID contractId, boolean status, User user);
        ApiResponse<ResPageable> getAllContractByUser(User user, String productName, ContractStatus contractStatus, int page, int size);
        ApiResponse<ResContract> getContractById(UUID contractId);



}
