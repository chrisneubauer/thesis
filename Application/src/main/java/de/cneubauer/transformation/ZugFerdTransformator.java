package de.cneubauer.transformation;

import de.cneubauer.domain.bo.LegalPerson;
import io.konik.PdfHandler;
import io.konik.validation.InvoiceValidator;
import io.konik.zugferd.Invoice;
import io.konik.zugferd.entity.*;
import io.konik.zugferd.entity.trade.*;
import io.konik.zugferd.entity.trade.item.*;
import io.konik.zugferd.profile.Profile;
import io.konik.zugferd.profile.ProfileVersion;
import io.konik.zugferd.unece.codes.TaxCode;
import io.konik.zugferd.unqualified.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.validation.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.Set;

import static com.neovisionaries.i18n.CountryCode.DE;
import static com.neovisionaries.i18n.CurrencyCode.EUR;
import static io.konik.zugferd.profile.ConformanceLevel.BASIC;
import static io.konik.zugferd.unece.codes.DocumentCode._380;
import static io.konik.zugferd.unece.codes.Reference.FC;
import static io.konik.zugferd.unece.codes.UnitOfMeasurement.UNIT;
import static org.apache.commons.lang3.time.DateUtils.addMonths;

/**
 * Created by Christoph Neubauer on 21.09.2016.
 * Basic transformation class that provides methods to convert invoice information to zugferd
 */
public class ZugFerdTransformator {

    private Logger logger = Logger.getLogger(this.getClass());

    void addMockInvoiceToPDF(String pdfPath, String pdfName) throws IOException {
        Invoice metaData = this.createMockInvoice();
        OutputStream outPdf = new FileOutputStream(".\\target\\test-classes\\generatedPDF\\" + pdfName + ".pdf");
        InputStream reader = this.getClass().getResourceAsStream(pdfPath);

        PdfHandler pdfHandler = new PdfHandler();
        pdfHandler.appendInvoice(metaData, reader, outPdf);
    }

    Invoice extractInvoiceFromMockPdf(String pdfName) {
        PdfHandler handler = new PdfHandler();
        InputStream inputZugferdPdfStream = getClass().getResourceAsStream("../../../../../target/test-classes/generatedPDF/" + pdfName + ".pdf");
        return handler.extractInvoice(inputZugferdPdfStream);
    }

    public void appendInvoiceToPdf(String pdfPath, String pdfName, Invoice i) throws IOException {
        OutputStream outPdf = new FileOutputStream(".\\target\\test-classes\\generatedPDF\\" + pdfName + ".pdf");
        InputStream reader = this.getClass().getResourceAsStream(pdfPath);

        PdfHandler pdfHandler = new PdfHandler();
        pdfHandler.appendInvoice(i, reader, outPdf);
    }

    public byte[] appendInvoiceToPdf(byte[] pdf, Invoice i) throws IOException {
        ByteArrayOutputStream outPdf = new ByteArrayOutputStream();
        InputStream reader = new ByteArrayInputStream(pdf);

        PdfHandler pdfHandler = new PdfHandler();
        pdfHandler.appendInvoice(i, reader, outPdf);
        return outPdf.toByteArray();
    }

    void validateMockInvoiceFromPdf(String pdfName) {
        //setup
        Invoice invoice = extractInvoiceFromMockPdf(pdfName);
        InvoiceValidator invoiceValidator = new InvoiceValidator();

        //execute
        Set<ConstraintViolation<Invoice>> violations = invoiceValidator.validate(invoice);

        for (ConstraintViolation<Invoice> violation : violations) {
            logger.log(Level.INFO, violation.getMessage() + " at: " + violation.getPropertyPath() );
        }
        //verify
        System.out.println("Violations: " + violations.size());
    }

    public Invoice createInvoice(de.cneubauer.domain.bo.Invoice invInfo) {
        Invoice invoice = new Invoice(BASIC);
        invoice.setHeader(new Header().setInvoiceNumber(String.valueOf(invInfo.getId())).setCode(_380));

        Trade trade = new Trade();
        TradeParty seller = this.convertLegalPersonToTradeParty(invInfo.getCreditor());

        TradeParty buyer = this.convertLegalPersonToTradeParty(invInfo.getDebitor());
        trade.setAgreement(
                new Agreement()
                        .setSeller(seller)
                        .setBuyer(buyer)
        );
        ZfDate deliveryDate = new ZfDateDay(invInfo.getIssueDate().getTime());
        trade.setDelivery(new Delivery(deliveryDate));
        return invoice;
    }

    private boolean isInvoiceValid(Invoice i) {
        InvoiceValidator invoiceValidator = new InvoiceValidator();

        //execute
        Set<ConstraintViolation<Invoice>> violations = invoiceValidator.validate(i);

        for (ConstraintViolation<Invoice> violation : violations) {
            logger.log(Level.INFO, violation.getMessage() + " at: " + violation.getPropertyPath() );
        }
        //verify
        System.out.println("Violations: " + violations.size());
        return violations.size() < 1;
    }

    private TradeParty convertLegalPersonToTradeParty(LegalPerson p) {
        TradeParty result = new TradeParty();
        if (p.getIsCompany()) {
            result.setName(p.getCompanyName() + " " + p.getCorporateForm());
        } else {
            result.setName(p.getName() + " " + p.getSurName());
        }
        Address a = new Address();
        if (p.getZipCode() > 0) {
            a.setPostcode(String.valueOf(p.getZipCode()));
        }
        if (p.getCity() != null) {
            a.setCity(p.getCity());
        }
        if (p.getStreet() != null) {
            a.setLineOne(p.getStreet());
        }
        //TODO: Get Country
        a.setCountry(DE);
        result.setAddress(a);

        return result;
    }

    public Invoice createFullConformalBasicInvoice(de.cneubauer.domain.bo.Invoice inv) {
        Invoice i = new Invoice(BASIC);

        Context con = new Context(BASIC);
        Profile guideline = new Profile(BASIC);
        guideline.setVersion(ProfileVersion.V1P0);
        con.setGuideline(guideline);

        Header h = new Header();
        //TODO: Application could also support other types except invoices
        h.setName("RECHNUNG");
        h.setInvoiceNumber(inv.getInvoiceNumber());
        h.setCode(_380);
        h.setIssued(new ZfDateDay(inv.getIssueDate().getTime()));

        Trade tr = new Trade();

        Agreement a = new Agreement();
        a.setBuyer(new TradeParty().setName(inv.getDebitor().toString()));
        a.setSeller(new TradeParty().setName(inv.getCreditor().toString()));

        Delivery d;
        if (inv.getDeliveryDate() == null) {
            d = new Delivery(new ZfDateDay(inv.getIssueDate().getTime()));
        } else {
            d = new Delivery(new ZfDateDay(inv.getDeliveryDate().getTime()));
        }

        MonetarySummation sum = new MonetarySummation();
        sum.setLineTotal(new Amount(BigDecimal.valueOf(inv.getLineTotal()), EUR));
        sum.setChargeTotal(new Amount(BigDecimal.valueOf(inv.getChargeTotal()), EUR));
        sum.setAllowanceTotal(new Amount(BigDecimal.valueOf(inv.getAllowanceTotal()), EUR));
        sum.setTaxBasisTotal(new Amount(BigDecimal.valueOf(inv.getTaxBasisTotal()), EUR));
        sum.setTaxTotal(new Amount(BigDecimal.valueOf(inv.getTaxTotal()), EUR));
        sum.setGrandTotal(new Amount(BigDecimal.valueOf(inv.getGrandTotal()), EUR));

        Settlement s = new Settlement();
        s.setCurrency(EUR);
        s.setMonetarySummation(sum);

        Item item = new Item();
        tr.addItem(item);
        tr.setAgreement(a);
        tr.setDelivery(d);
        tr.setSettlement(s);

        i.setContext(con);
        i.setHeader(h);
        i.setTrade(tr);

        if (this.isInvoiceValid(i)) {
            return i;
        } else {
            return null;
        }
    }

    Invoice createMockInvoice() {

        ZfDate today = new ZfDateDay();
        ZfDate nextMonth = new ZfDateMonth(addMonths(today, 1));

        Invoice invoice = new Invoice(BASIC);
        invoice.setHeader(new Header()
                .setInvoiceNumber("20131122-42")
                .setCode(_380)
                .setIssued(today)
                .setName("Rechnung"));

        Trade trade = new Trade();
        trade.setAgreement(new Agreement()
                .setSeller(new TradeParty()
                        .setName("Seller Inc.")
                        .setAddress(new Address("80331", "Marienplatz 1", "München", DE))
                        .addTaxRegistrations(new TaxRegistration("DE122...", FC)))
                .setBuyer(new TradeParty()
                        .setName("Buyer Inc.")
                        .setAddress(new Address("50667", "Domkloster 4", "Köln", DE))
                        .addTaxRegistrations(new TaxRegistration("DE123...", FC))));

        trade.setDelivery(new Delivery(nextMonth));

        ItemTax itemTax = new ItemTax();
        itemTax.setPercentage(BigDecimal.valueOf(19));
        itemTax.setType(TaxCode.VAT);

        trade.addItem(new Item()
                .setProduct(new Product().setName("Saddle"))
                .setAgreement(new SpecifiedAgreement().setGrossPrice(new GrossPrice(new Amount(100, EUR))).setNetPrice(new Price(new Amount(100, EUR))))
                .setSettlement(new SpecifiedSettlement().addTradeTax(itemTax))
                .setDelivery(new SpecifiedDelivery(new Quantity(1, UNIT))));

        trade.setSettlement(new Settlement()
                .setPaymentReference("20131122-42")
                .setCurrency(EUR)
                .addPaymentMeans(new PaymentMeans()
                        .setPayerAccount(new DebtorFinancialAccount("DE01234.."))
                        .setPayerInstitution(new FinancialInstitution("GENO...")))
                .setMonetarySummation(new MonetarySummation()
                        .setLineTotal(new Amount(100, EUR))
                        .setChargeTotal(new Amount(0,EUR))
                        .setAllowanceTotal(new Amount(0, EUR))
                        .setTaxBasisTotal(new Amount(100, EUR))
                        .setTaxTotal(new Amount(19, EUR))
                        .setDuePayable(new Amount(119, EUR))
                        .setTotalPrepaid(new Amount(0, EUR))
                        .setGrandTotal(new Amount(119, EUR))));

        invoice.setTrade(trade);

        return invoice;
    }
}
